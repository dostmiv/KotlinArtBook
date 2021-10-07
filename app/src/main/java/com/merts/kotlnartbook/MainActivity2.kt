package com.merts.kotlnartbook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.jar.Manifest
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.view.Menu
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.ByteArrayOutputStream

class MainActivity2 : AppCompatActivity() {
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_art,menu)
        return super.onCreateOptionsMenu(menu)
    }
    fun save(view: View) {
        val artName = artText.text.toString()
        val painterName = painterName.text.toString()

        val outputStream = ByteArrayOutputStream()
        selectedBitmap?.compress(Bitmap.CompressFormat.PNG,50,outputStream)
        val byteArray = outputStream.toByteArray()
        try{
            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artName VARCHAR, painterName VARCHAR,image BLOB)")
            val sqlString = "INSERT INTO arts (artName,painterName,image) VALUES (?,?,?,?)"
            val statement = database.compileStatement(sqlString)

            statement.bindString(1,artName)
            statement.bindString(2,painterName)
            statement.bindBlob(3,byteArray)

            statement.execute()

        }catch (e: Exception){
            e.printStackTrace()
        }
        finish()

    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery, 2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2  && resultCode == Activity.RESULT_OK && data != null ){
            selectedPicture = data.data
            if (Build.VERSION.SDK_INT >= 28){
                val source = ImageDecoder.createSource(this.contentResolver,selectedPicture!!)
                val bitmap= ImageDecoder.decodeBitmap(source)
                imageView.setImageBitmap(bitmap)
            }else{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedPicture)
                imageView.setImageBitmap(bitmap)
            }


        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
















