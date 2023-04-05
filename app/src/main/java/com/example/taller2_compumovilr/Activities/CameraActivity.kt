package com.example.taller2_compumovilr.Activities

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.taller2_compumovilr.databinding.ActivityCameraBinding

import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private val TAG: String =
        CameraActivity::class.java.name
    val PERMISSIONS_REQUEST_CAMERA = 1001

    private lateinit var binding: ActivityCameraBinding

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_VIDEO_CAPTURE = 2
    private var pictureImagePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        verifyPermissions()
        binding?.takeButton?.setOnClickListener {
            takePictureOrVideo()
        }
    }
    private fun takePictureOrVideo(){
        if (binding.videoToggle.isChecked){
            takeVideo()
        }else{
            takePhoto()
        }
    }
    private fun takePhoto(){

    }
    private fun takeVideo(){

    }
    private fun verifyPermissions(){
        Log.d(TAG,"verifyPermission: asking user for permissions")
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[0]
            )== PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[1]
            ) == PackageManager.PERMISSION_GRANTED&&
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[2]
            ) == PackageManager.PERMISSION_GRANTED){
            //ACEPTADO
        }else{
            ActivityCompat.requestPermissions(this,permissions,1)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        verifyPermissions()
    }
}