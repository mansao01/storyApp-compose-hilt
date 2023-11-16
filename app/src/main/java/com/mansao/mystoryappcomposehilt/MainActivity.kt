package com.mansao.mystoryappcomposehilt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.mansao.mystoryappcompose.data.local.model.LocationModel
import com.mansao.mystoryappcomposehilt.ui.AuthViewModel
import com.mansao.mystoryappcomposehilt.ui.MyStoryApp
import com.mansao.mystoryappcomposehilt.ui.theme.MyStoryAppComposeHiltTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private var locationCallback: LocationCallback? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequired = false
    private var locationModel by mutableStateOf(
        LocationModel(0.0, 0.0)
    )
    private var isLocationEnabled by mutableStateOf(false)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyStoryAppComposeHiltTheme {
                // A surface container using the 'background' color from the theme
                val context = LocalContext.current
                SetupLocationServices(context)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination = authViewModel.startDestination

                    MyStoryApp(
                        startDestination = startDestination.value,
                        location = locationModel,
                        locationEnabled = isLocationEnabled
                    )
                }
            }
        }
    }

    @Composable
    private fun SetupLocationServices(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (hasLocationPermissions(context, permissions)) {
            getCurrentLocation()
            isLocationEnabled = true

        } else {
            RequestLocationPermissions(context, permissions)
        }
    }

    private fun hasLocationPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @Composable
    private fun RequestLocationPermissions(context: Context, permissions: Array<String>) {
        val launcherMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val areGranted = permissionsMap.values.all { it }
            if (areGranted) {
                locationRequired = true
                getCurrentLocation()
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            launcherMultiplePermissions.launch(permissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object :
            CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken =
                CancellationTokenSource().token


            override fun isCancellationRequested(): Boolean = false
        })?.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                locationModel = LocationModel(latitude, longitude)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            getCurrentLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}