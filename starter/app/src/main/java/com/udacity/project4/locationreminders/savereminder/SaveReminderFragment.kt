package com.udacity.project4.locationreminders.savereminder

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    companion object{
        internal const val ACTION_GEOFENCE_EVENT = "actionGeofenceEvent"
    }
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient : GeofencingClient
    private val geofencePendingIntent : PendingIntent by lazy {
        val intent = Intent(requireActivity(),GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireActivity(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }
    data class LandmarkDataObject(val id:String , val latLong : LatLng)
    internal object GeofencingConstants {
        const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
        const val GEOFENCE_RADIUS_IN_METER = 100f
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS : Long = TimeUnit.HOURS.toMillis(1)
        val LANDMARK_DATA = arrayOf(
            LandmarkDataObject(
                 "cairo universty",
                LatLng(100.00,100.00)


            )
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val selectedPoi = _viewModel.selectedPOI.value




//            Done: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_TURN_DEVICE_LOCATION_ON ){
            checkDeviceLocationSettingsAndStartGeofence(

            )
        }
    }

    fun onNewIntent(intent:Intent?){

        val extras = intent?.extras
        if(extras != null){

            if(extras.containsKey(GeofencingConstants.EXTRA_GEOFENCE_INDEX))
                checkPermissionAndStartingGeofencing()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()||grantResults[LOCATION_PERMISSION_INDEX]==PackageManager.PERMISSION_DENIED ||(
                    requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                            grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX]==
                            PackageManager.PERMISSION_DENIED
                )){
            Toast.makeText(requireContext(),"you need to grant location permission",Toast.LENGTH_LONG)
        }else{
            checkDeviceLocationSettingsAndStartGeofence()
        }

    }


    private fun addGeofenceForClue() {
        val currentGeofenceData = GeofencingConstants.LANDMARK_DATA[0]
        val geofence = Geofence.Builder().setRequestId(currentGeofenceData.id)
            .setCircularRegion(
                currentGeofenceData.latLong.latitude,
                currentGeofenceData.latLong.longitude,
                GeofencingConstants.GEOFENCE_RADIUS_IN_METER
            )
            .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent)?.run{
            addOnCompleteListener{
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Done: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@addOnCompleteListener
                }
                geofencingClient.addGeofences(geofencingRequest,geofencePendingIntent).run{
                    addOnSuccessListener{
                        Toast.makeText(requireContext(),"geofences added",Toast.LENGTH_LONG).show()
                        _viewModel.navigationCommand.value = NavigationCommand.Back

                    }
                    addOnFailureListener{
                        Toast.makeText(requireContext(),"geofence not added",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
    private fun checkPermissionAndStartingGeofencing(){
        if(foregroundAndBackgroundLocationPermissionApproved()){
            checkDeviceLocationSettingsAndStartGeofence()
        }else{
            requestForegroundAndBackgroundLocationPermissions()
        }
    }
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true){
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask= settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener{
            exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(requireActivity(), REQUEST_TURN_DEVICE_LOCATION_ON)
                }catch (sendEX: IntentSender.SendIntentException){
                    Log.d(TAG,"Error getting Location Setting")
                }
            }else{
                Toast.makeText(requireContext(),"Location permission must be enabled",Toast.LENGTH_LONG)


            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if(it.isSuccessful){
                addGeofenceForClue()
            }
        }

    }
    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved():Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)

                )
        val backgroundPermissionApproved =
            if(runningQOrLater){
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }
    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions(){
        if(foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningQOrLater->{
                permissionsArray += android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
         else->  REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        ActivityCompat.requestPermissions(requireActivity(),permissionsArray,resultCode)
    }

    private fun removeGeofences(){
        if(!foregroundAndBackgroundLocationPermissionApproved()){
            return
        }
        geofencingClient.removeGeofences(geofencePendingIntent)?.run{
            addOnSuccessListener{
                Log.d(TAG,"geofences is removed")
                Toast.makeText(requireContext(),"geofence is removed",Toast.LENGTH_LONG)
            }
            addOnFailureListener{
                Log.d(TAG,"geofence failed to remove")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
        removeGeofences()
    }
}
  private const val TAG = "SaveReminderFragment"
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
