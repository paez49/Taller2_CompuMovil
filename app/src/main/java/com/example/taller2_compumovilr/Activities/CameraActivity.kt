package com.example.taller2_compumovilr.Activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2_compumovilr.databinding.ActivityCameraBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
  private val TAG: String =
    CameraActivity::class.java.name

companion object{
  private const val CAMERA_PERMISSION=100
  private const val STORAGE_PERMISSION=101
}
  private lateinit var binding: ActivityCameraBinding


  private var pictureImagePath: Uri? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCameraBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
    binding?.takeButton?.setOnClickListener {
      if(ContextCompat.checkSelfPermission(this,
          Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
        Log.i("Permission camera:","Permisos aceptados")
      }else{
        requestCameraPermission()
      }
    }
    binding?.selectGalleryButton?.setOnClickListener {
      if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Log.i("Permission storage:","Permisos aceptados")
        takePictureOrVideo()
      } else {
        requestStoragePermission()
      }

    }
  }

  private fun takePictureOrVideo() {
    if (binding.videoToggle.isChecked) {
      takeVideo()
    } else {
      takePhoto()
    }
  }

  private fun takePhoto() {


  }

  private fun takeVideo() {

  }

  private fun requestCameraPermission(){
    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
     AlertDialog.Builder(this)
        .setTitle("Permiso denegado")
        .setMessage("El permiso  de camara es necesario para el funcionamiento de la aplicación")
        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
          ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION)
        }).setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

        }).create().show()
    }else{
      ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
    }
  }
  private fun requestStoragePermission(){
    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
      AlertDialog.Builder(this)
        .setTitle("Permiso denegado")
        .setMessage("El permiso de almacenamiento es necesario para el funcionamiento de la aplicación")
        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
          ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
              Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION)
        }).setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->

        }).create().show()
    }else{
      ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION)
    }
  }
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if(requestCode == CAMERA_PERMISSION){
      if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
        Log.i("Camera permission","Permiso de cámara aceptado")
      }else{
        val snackbar = Snackbar.make(binding.root, "Permiso de camara no fue otorgado", Snackbar.LENGTH_LONG)
        snackbar.setAction("Cerrar") {
        }
        snackbar.show()
        Log.i("Camera permission","Permiso de cámara no aceptado")
      }
    }else if(requestCode== STORAGE_PERMISSION){
      if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED &&
        grantResults[1]== PackageManager.PERMISSION_GRANTED){
        Log.i("Storage permission","Permiso de almacenamiento aceptados")
      }else{
        val snackbar = Snackbar.make(binding.root, "Permiso de almacenamiento no fue otorgado", Snackbar.LENGTH_LONG)
        snackbar.setAction("Cerrar") {
        }
        snackbar.show()
        Log.i("Storage permission","Permiso de almacenamiento no aceptados")
      }

    }
  }
}
