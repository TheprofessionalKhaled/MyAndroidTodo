package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var navController : NavController
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    var selectedLatitude = 10.000
    var selectedLongitude = 10.000
    var selectedLocationName = "a Default one"
    private val TAG = SelectLocationFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private val defaultZoom = 15
    private var permissionDenied = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        navController = findNavController()


        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        Done: add the map setup implementation
//        Done: zoom to the user location after taking his permission
//        Done: add style to the map
//        Done: put a marker to location that the user selected


//        Done: call this function after the user confirms on the selected location
        onLocationSelected()

        binding.floatingActionButton.setOnClickListener{
            onLocationSelected()
        }
        return binding.root
    }

    private fun onLocationSelected() {
        //        Done: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        _viewModel.latitude.value = selectedLatitude
        _viewModel.longitude.value= selectedLongitude
        _viewModel.reminderSelectedLocationStr.value = selectedLocationName
        navController.popBackStack()

    }
    override fun onMapReady(googleMap: GoogleMap){
        val latitude = 37.422
        val longitude = -122.084
        val zoomLevel = 15f
        val overlaySize = 100f
        val homeLatLng = LatLng(latitude,longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng,zoomLevel))
        map.addMarker(MarkerOptions().position(homeLatLng))
        val googleOverlay = GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background))
            .position(homeLatLng,overlaySize)
        map.addGroundOverlay(googleOverlay)
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()
        getDeviceLocation()
    }

    private fun getDeviceLocation(){
        try {


        if (!permissionDenied){
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val lastLocation = task.result
                if (lastLocation != null) {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            ), defaultZoom.toFloat()
                        )
                    )
                }


            } else {
                val defaultLocation = LatLng(0.0, 0.0)
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        defaultLocation,
                        defaultZoom.toFloat()
                    )
                )

            }
        }}
        }catch (e:SecurityException){
            Log.e("Exception : %s",e.message,e)
        }
    }



    private fun enableMyLocation() {
        if (isPermissionGranted()){
            if (map.isMyLocationEnabled == false){
                map.isMyLocationEnabled == true
            }else{
                map.isMyLocationEnabled == true
            }
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
        }
    }

    private fun isPermissionGranted(): Boolean {
return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)===PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle(map: GoogleMap) {
try {
    val  success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style))
    if (!success){
        Log.e(TAG,"styling failed")
    }
}catch (e:Resources.NotFoundException){
    Log.e(TAG,"cannot find style",e)
}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode ==REQUEST_LOCATION_PERMISSION){
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                enableMyLocation()
            }
        }
    }



    private fun setPoiClick(map: GoogleMap) {
map.setOnPoiClickListener { poi ->
    selectedLatitude = poi.latLng.latitude
    selectedLongitude = poi.latLng.longitude
    selectedLocationName = poi.name
    val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
    poiMarker.showInfoWindow()
}
    }

    private fun setMapLongClick(map: GoogleMap) {
map.setOnMapClickListener {
    LatLng ->
    selectedLatitude = LatLng.latitude
    selectedLongitude = LatLng.longitude
    selectedLocationName = "a Default one"
    val snippet = String.format(Locale.getDefault(),"Lat:%1$.5f,Long :%2$.gf",LatLng.latitude,LatLng.longitude)
    map.addMarker(MarkerOptions()
        .position
    (LatLng).title(getString(R.string.dropped_pin)).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
}
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Done: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID

            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE

            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true

        }
        else -> super.onOptionsItemSelected(item)
    }




}
