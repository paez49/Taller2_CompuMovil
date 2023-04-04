package com.example.taller2_compumovilr.Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.taller2_compumovilr.databinding.ActivityCameraBinding
import com.example.taller2_compumovilr.services.PermissionService
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private val TAG: String =
        CameraActivity::class.java.name
    private lateinit var binding: ActivityCameraBinding

    @Inject
    var permissionService: PermissionService?= null

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_VIDEO_CAPTURE = 2
    private var pictureImagePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.i("EL SERVICIO:",permissionService.toString())
        binding?.takeButton?.setOnClickListener {
            if (!permissionService?.isMCameraPermissionGranted!!) {
                permissionService!!.getCameraPermission(this)
            } else {
                takePictureOrVideo()
            }
        }
    }
    private fun takePictureOrVideo(){
        if (binding.videoToggle.isChecked()){
            takeVideo()
        }else{
            takePhoto()
        }
    }
    private fun takePhoto(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Create temp file for image result
        val timeStamp = DateFormat.getDateInstance().format(Date())
        val imageFileName = String.format("%s.jpg", timeStamp)
        val imageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)
        pictureImagePath = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            imageFile
        )
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
        try {
            startActivityForResult(
                pictureIntent,
                REQUEST_IMAGE_CAPTURE
            )
        } catch (e: ActivityNotFoundException) {
            Log.e(
                TAG,
                e.localizedMessage
            )
        }
    }
    private fun takeVideo(){
        
    }
}