package com.example.taller2_compumovilr.Activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.taller2_compumovilr.databinding.ActivityCameraBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.DateFormat.getDateInstance
import java.util.*


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
    binding.videoPresentation.visibility = View.GONE
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

      } else {
        //requestStoragePermission()
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
    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    //Create temp file for image result
    //Create temp file for image result
    val timeStamp: String = getDateInstance().format(Date())
    val imageFileName = String.format("%s.jpg", timeStamp)
    val imageFile =
      File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + imageFileName)
    pictureImagePath = FileProvider.getUriForFile(
      this,
      "com.example.android.file-provider",
      imageFile
    )
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
    try {
      startActivityForResult(takePictureIntent, 1)
    } catch (e: ActivityNotFoundException) {
      Log.e(TAG, e.localizedMessage)
    }

  }

  private fun takeVideo() {
    val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
    if (takeVideoIntent.resolveActivity(this.packageManager) != null) {
      try {
        startActivityForResult(takeVideoIntent, 2)
      } catch (e: ActivityNotFoundException) {
        Log.e(TAG, e.localizedMessage)
      }
    } else {
      Toast.makeText(this,"ESKERE",Toast.LENGTH_SHORT)
    }
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
        takePictureOrVideo()
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
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 1 && resultCode == RESULT_OK) {
      binding.imagePresentation.visibility = View.VISIBLE
      binding.videoPresentation.visibility = View.GONE

      binding.imagePresentation.setImageDrawable(null);
      binding.imagePresentation.setImageURI(pictureImagePath)
      binding.imagePresentation.scaleType = ImageView.ScaleType.FIT_CENTER
      binding.imagePresentation.adjustViewBounds = true
    }else if (requestCode == 2 && resultCode == RESULT_OK) {
      binding.imagePresentation.visibility = View.GONE
      binding.videoPresentation.visibility = View.VISIBLE

      binding.videoPresentation.setVideoURI(null)
      binding.videoPresentation.setVideoURI(data?.data)
      binding.videoPresentation.setMediaController(MediaController(this))
      binding.videoPresentation.start()
    }
    if (requestCode == 2 && resultCode == RESULT_CANCELED){
      binding.videoPresentation.visibility = View.GONE
    }
    if (requestCode == 1 && resultCode == RESULT_CANCELED){
      binding.imagePresentation.visibility = View.GONE
    }
  }
}
