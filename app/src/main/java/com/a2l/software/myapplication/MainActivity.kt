package com.a2l.software.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import android.app.Activity
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log

import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.a2l.software.myapplication.databinding.ActivityMainBinding
import com.a2l.software.myapplication.model.ImageEntity
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File

class MainActivity : AppCompatActivity() {

    private val listUriImageSelected = arrayListOf<ImageEntity>()
    private var tempFileTakeCamera: File? = null

    // declaring width and height
    // for our PDF file.
    val pageHeight = 1120
    val pagewidth = 792

    private var uri: Uri? = null

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private val someActivityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                uri = it
                Log.d("TAGs", "result data is exists: $it")
                listUriImageSelected.add(0, ImageEntity(System.currentTimeMillis(), it.toString()))
                adapter = ScreenSlidePagerAdapter(this, listUriImageSelected)
                binding.viewpager.adapter = adapter
                TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->

                }.attach()
            }
        }
    }

    private val takeCameraResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            tempFileTakeCamera?.let {
                Log.d("TAGs", "take camera: $it")
                listUriImageSelected.add(0, ImageEntity(System.currentTimeMillis(), it.toUri().toString()))
                adapter = ScreenSlidePagerAdapter(this, listUriImageSelected)
                binding.viewpager.adapter = adapter
                TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->

                }.attach()
            }
        }
    }
    private lateinit var binding: ActivityMainBinding
    private var adapter: ScreenSlidePagerAdapter? = null

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = ScreenSlidePagerAdapter(this, listUriImageSelected)
        binding.tvOpenGallery.setOnClickListener {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

//            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            someActivityResultLauncher.launch(getIntent)
        }
        binding.tvOpenCamera.setOnClickListener {
            if (Helper.isCheckCameraHardware(this)) {
                // open camera
                tempFileTakeCamera = File("${getExternalFilesDir(null)}/imgShot")
                tempFileTakeCamera?.let {
                    val photoURI = FileProvider.getUriForFile(this, "${packageName}.fileprovider", it)
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        .apply { putExtra(MediaStore.EXTRA_OUTPUT, photoURI) }
                    takeCameraResultLauncher.launch(intent)
                }
            } else {
                Toast.makeText(this, "Device is not support camera", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSavePdf.setOnClickListener {
//            val pdfDocument: PdfDocument = PdfDocument()
//
//            val paint = Paint()
//            val text = Paint()
//
//            val myPageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create()
//
//            val myPage: PdfDocument.Page = pdfDocument.startPage(myPageInfo)
//
//            val canvas: Canvas = myPage.canvas
//            val bmp = BitmapFactory.decodeResource(resources, R.drawable.demo)
//            val scaledBmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
//            canvas.drawBitmap(scaledBmp, 56F, 40F, paint);
        }
        binding.tvInfoFile.setOnClickListener {
            uri?.let {
                val file = Helper.getFile(this, it) ?: return@setOnClickListener
                if (!file.exists()) return@setOnClickListener
                val sizeB = file.length()
                var sizeMB = sizeB.toDouble() / 1024 / 1024
                if (sizeMB > 2) {
//                    Toast.makeText(this, "Copy file!", Toast.LENGTH_SHORT).show()
                    val rootFile =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val tempFile = File(rootFile, "temp.png")
                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                    tempFile.createNewFile()

                    if (Helper.copyFile(file, tempFile)) {
                        // resize
                        while (sizeMB > 2) {
                            Toast.makeText(this, "Resize file!", Toast.LENGTH_SHORT).show()
                            Log.d("TAGs", "Resize. sizeMB = $sizeMB")
                            val bmOptions = BitmapFactory.Options()
                            bmOptions.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(tempFile.path, bmOptions)
                            bmOptions.inJustDecodeBounds = false
                            bmOptions.inSampleSize = 2
                            BitmapFactory.decodeFile(tempFile.path, bmOptions)
                            sizeMB = tempFile.length().toDouble() / 1024 / 1024
                        }
                    } else {
                        Toast.makeText(this, "Error copy file. File size = $sizeMB", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "File size = $sizeMB", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.viewpager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = this@MainActivity.adapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->

        }.attach()
    }
}