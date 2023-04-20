package com.example.taller2_compumovilr.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import java.util.*


class mapsActivity : AppCompatActivity(), OnMapReadyCallback,OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener{
    private lateinit var map:GoogleMap
    companion object{
        const val REQUEST_CODE_LOCATION = 303
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(com.example.taller2_compumovilr.R.string.google_maps_key), Locale.US);
        }
        super.onCreate(savedInstanceState)
        setContentView(com.example.taller2_compumovilr.R.layout.activity_maps)

        val buscarSitio = findViewById<AutoCompleteTextView>(com.example.taller2_compumovilr.R.id.buscarSitio)
        buscarSitio.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && v.text.toString().isNotEmpty()) {
                val busqueda = v.text.toString()

                v.setText("")
                buscarLugar(busqueda)
                return@OnEditorActionListener true
            }
            false
        })


        createFragment()
    }
    private fun createFragment(){
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(com.example.taller2_compumovilr.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map=p0
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableLocation()
    }
    private fun buscarLugar(query: String) {

        val placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()
        placesClient.findAutocompletePredictions(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                val predictions = response.autocompletePredictions
                if (!predictions.isNullOrEmpty()) {
                        val prediction = predictions[0]
                        val placeId = prediction.placeId
                        val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
                        val request = FetchPlaceRequest.builder(placeId, fields).build()
                        placesClient.fetchPlace(request).addOnSuccessListener { response ->
                        val place = response.place
                            print(place.latLng)
                            if(place.latLng!= null && place.name != null) {
                                Toast.makeText(this, place.name, Toast.LENGTH_SHORT).show()
                                createMarker(place.latLng, place.name)
                            }
                        }
                }
                else
                    Toast.makeText(this,"No se puede ubicar ese lugar", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun createMarker(coordinates: LatLng,lugar:String) {
        val marker:MarkerOptions = MarkerOptions().position(coordinates).title(lugar)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates,18f),
            4000,
            null
        )

    }

    private fun isLocationPermisionGranted() = ContextCompat.checkSelfPermission(
            this,Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermisionGranted())
        {
            //SI
            map.isMyLocationEnabled=true
        }
        else
        {
            //No
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
        else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }
            else
            {
                Toast.makeText(this,"Ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()
            }
            else ->{}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if(!isLocationPermisionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos",Toast.LENGTH_SHORT).show()

        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Centrando en tu ubicación actual",Toast.LENGTH_SHORT).show()
        return false
    }

    @SuppressLint("MissingPermission")
    override fun onMyLocationClick(p0: Location) {


// Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME)
// Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            val placesClient = Places.createClient(this)
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    val placeLikelihood: PlaceLikelihood =response.placeLikelihoods[0]
                    var msg = "Te encuentras cerca de ${placeLikelihood.place.name}"
                    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()

                } else {
                    val exception = task.exception
                    if (exception is ApiException) {

                    }
                }
            }
        } else {
            requestLocationPermission()
        }


    }

}