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

  private const val CAMERA_REQUEST=1
  private const val VIDEO_REQUEST=2

  private const val GALLERY_PHOTO_REQUEST=3
  private const val GALLERY_VIDEO_REQUEST=4
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
          Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
        Log.i("Permission camera:","asasdasd aceptados")
        takePhotoOrVideo()
      }else{
        requestCameraPermission()
      }
    }
    binding?.selectGalleryButton?.setOnClickListener {
      if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this,
          Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Log.i("Permission storage:","asdasdasdasd aceptados")
        galleryPhotoOrVideo()
      } else {
        requestStoragePermission()
      }

    }
  }

  private fun takePhotoOrVideo() {
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
      startActivityForResult(takePictureIntent, CAMERA_REQUEST)
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
        startActivityForResult(takeVideoIntent, VIDEO_REQUEST)
      } catch (e: ActivityNotFoundException) {
        Log.e(TAG, e.localizedMessage)
      }
    } else {
      Toast.makeText(this,"ESKERE",Toast.LENGTH_SHORT)
    }
  }
  private fun galleryPhotoOrVideo(){
    if (binding.videoToggle.isChecked) {
      galleryVideo()
    } else {
      galleryPhoto()
    }
  }
  private fun galleryPhoto(){
    val i = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    startActivityForResult(i, GALLERY_PHOTO_REQUEST)
  }
  private fun galleryVideo(){
    val i = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    startActivityForResult(i, GALLERY_VIDEO_REQUEST)
  }
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if(resultCode == RESULT_CANCELED){
      binding.imagePresentation.visibility = View.GONE
      binding.videoPresentation.visibility = View.GONE
    }else{
      when(requestCode) {
        CAMERA_REQUEST -> {
          if(resultCode == RESULT_OK) {
            binding.imagePresentation.visibility = View.VISIBLE
            binding.videoPresentation.visibility = View.GONE

            binding.imagePresentation.setImageDrawable(null);
            binding.imagePresentation.setImageURI(pictureImagePath)
            binding.imagePresentation.scaleType = ImageView.ScaleType.FIT_CENTER
            binding.imagePresentation.adjustViewBounds = true

          }
        }
        VIDEO_REQUEST -> {
          if(resultCode == RESULT_OK) {
            binding.imagePresentation.visibility = View.GONE
            binding.videoPresentation.visibility = View.VISIBLE

            binding.videoPresentation.setVideoURI(null)
            binding.videoPresentation.setVideoURI(data?.data)
            binding.videoPresentation.setMediaController(MediaController(this))
            binding.videoPresentation.start()
          }
        }
        GALLERY_PHOTO_REQUEST -> {
          val selectedImage: Uri? = data?.data
          val imageType = contentResolver.getType(selectedImage!!)
          if (imageType?.startsWith("image/") == true) {
            binding.imagePresentation.visibility = View.VISIBLE
            binding.videoPresentation.visibility = View.GONE
            // Es una foto
            binding.imagePresentation.setImageURI(selectedImage)
          } else {
            //No es una foto
            binding.videoPresentation.visibility = View.GONE
            Toast.makeText(this,"Para seleccionar un video y presentarlo, activa el toggle.",Toast.LENGTH_SHORT).show()
          }
        }
        GALLERY_VIDEO_REQUEST->{
          val selectedVideo: Uri? = data?.data
          val videoType = contentResolver.getType(selectedVideo!!)
          if (videoType?.startsWith("video/") == true) {
            binding.imagePresentation.visibility = View.GONE
            binding.videoPresentation.visibility = View.VISIBLE
            // Es un video
            binding.videoPresentation.setVideoURI(null)
            binding.videoPresentation.setVideoURI(selectedVideo)
            binding.videoPresentation.setMediaController(MediaController(this))
            binding.videoPresentation.start()
          } else {
            //No es un video
            binding.videoPresentation.visibility = View.GONE
            Toast.makeText(this,"Para seleccionar una foto y presentarla, desactiva el toggle.",Toast.LENGTH_SHORT).show()
          }
        }
      }
    }

    }


  //PERMISOS
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
        takePhotoOrVideo()
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
        galleryPhotoOrVideo()
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
