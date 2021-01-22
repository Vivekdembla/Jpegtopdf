package com.example.jpegtopdf

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Downloads
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle.*
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), imageIconClicked{
    lateinit var progressBar:ProgressBar
    lateinit var nameofpdf:String
    lateinit var dateofpdf:String
    lateinit var level2:ImageView
    lateinit var guideline7:Guideline
    lateinit var B_W:Button
    lateinit var delete:Button
    private val PICK_IMAGE = 100
    var REQUEST_IMAGE_CAPTURE = 1
    var imageList:ArrayList<Bitmap> = ArrayList()
    val filterlist:ArrayList<Bitmap> = ArrayList()
    lateinit var image: ImageView
    lateinit var currentPhotoPath: String
    private lateinit var mAdapter: ImageListAdapter

    lateinit var Capture: Button
    lateinit var PickFromGallery:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.x)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        level2=findViewById(R.id.level2)
        Capture=findViewById(R.id.Capture)
        PickFromGallery= findViewById(R.id.PickFromGallery)
        image = findViewById(R.id.image)
        delete = findViewById(R.id.Delete)
        B_W = findViewById(R.id.B_W)
        guideline7 = findViewById(R.id.guideline7)
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapter= ImageListAdapter(this)
        recyclerView.adapter=mAdapter

    }

    fun onClickingCapture(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            dispatchTakePictureIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                progressBar.visibility = VISIBLE
                GlobalScope.launch(Dispatchers.IO) {
                setPic()
                withContext(Dispatchers.Main) {
                    mAdapter.updateList(imageList)
                    progressBar.visibility = GONE
                    mAdapter.refresh()
                    delete.visibility = VISIBLE
                    B_W.visibility = VISIBLE
                    level2.visibility = VISIBLE
                    image.setImageBitmap(imageList[mAdapter.CP])
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            progressBar.visibility = VISIBLE
            level2.visibility = VISIBLE
            GlobalScope.launch(Dispatchers.IO) {
                val imageUri = data?.data
                withContext(Dispatchers.Main) {
                    image.setImageURI(imageUri)
                    progressBar.visibility = GONE
                }
                val bitmap = (image.drawable as BitmapDrawable).bitmap
                val scaled: Bitmap
                if (bitmap.height > bitmap.width) {
                    val x: Float = bitmap.height.toFloat() / A2.height
                    scaled = getResizedBitmap(bitmap, (bitmap.width / x).toInt() * 2, A2.height.toInt())

                } else {
                    val x: Float = bitmap.width.toFloat() / A2.width
                    scaled =
                        getResizedBitmap(bitmap, A2.width.toInt(), (bitmap.height / x).toInt() * 2)
                }

                if (mAdapter.CP < imageList.size) {
                    imageList.add(mAdapter.CP + 1, scaled)
                    val x = FilterBitmap(scaled)
                    filterlist.add(mAdapter.CP + 1, x)
                } else {
                    imageList.add(scaled)
                    val x = FilterBitmap(scaled)
                    filterlist.add(x)
                }
                withContext(Dispatchers.Main) {
                    mAdapter.updateList(imageList)
                    delete.visibility = VISIBLE
                    B_W.visibility = VISIBLE
                    mAdapter.refresh()
                    progressBar.visibility = GONE
                    image.setImageBitmap(imageList[mAdapter.CP])
                }
            }
        }
    }

    private fun setPic() {
        Log.i("this", "SET PIC K ANDR")
        val targetW: Int = image.width
        val targetH: Int = image.height

        val bmOptions = BitmapFactory.Options().apply {
            Log.i("this", "FACTORY PIC K ANDR")

            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inScaled = false
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            Log.e("this", "DECODE FILE K ANDR")
            val scaled : Bitmap
            if(bitmap.height>bitmap.width){
                val x:Float = bitmap.height.toFloat()/ A2.height
                scaled = getResizedBitmap(bitmap, (bitmap.width / x).toInt(), A2.height.toInt())
            }
            else{
                val x:Float = bitmap.width.toFloat()/A2.width
                scaled = getResizedBitmap(bitmap, A2.width.toInt(), (bitmap.height / x).toInt())
            }

            if(mAdapter.CP<imageList.size) {
                imageList.add(mAdapter.CP + 1, scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(mAdapter.CP + 1, x)
            }
            else{
                imageList.add(scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(x)
            }
        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

//         "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
//        val resizedBitmap = Bitmap.createScaledBitmap(bm,width/8,height/8,false)
        bm.recycle()
        return resizedBitmap
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {

                        null
                    }
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.jpegtopdf.provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)

                    }
            }
        }
    }
    fun createImageFile(): File {

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                currentPhotoPath = absolutePath
            }
    }

    override fun onIconClicked(item: Bitmap) {
        image.setImageBitmap(item)
    }

    fun MakePdf(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            CreatePdf(imageList)
            withContext(Dispatchers.Main) {
                val intent = Intent(this@MainActivity, ShowPdfActivity::class.java)
                intent.putExtra("FileName", nameofpdf)
                intent.putExtra("FileDate", dateofpdf)
                startActivity(intent)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(
                this,
                "File Path: ${Environment.DIRECTORY_DOWNLOADS}/Pdf_list",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            Toast.makeText(
                this,
                "File Path: ${getExternalFilesDir(null)}/my file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }




    private fun CreatePdf(list: ArrayList<Bitmap>){
        val document = PDDocument()
        for (i in 0 until list.size) {
            val page = PDPage(A2)
            document.addPage(page)
            val contentStream = PDPageContentStream(document, page)
            val ximage = JPEGFactory.createFromImage(document, list[i])

            contentStream.drawImage(
                ximage,
                ((A2.width - list[i].width) / 2),
                ((A2.height - list[i].height) / 2) + 50,
                list[i].width.toFloat(),
                list[i].height.toFloat() - 100
            )

            contentStream.close()
        }
        var outputStream: OutputStream?
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val timeStamp2: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(Date())
        val imageFileName = "PDF_$timeStamp"
        val fileName= "$imageFileName.pdf"
        nameofpdf=fileName
        dateofpdf=timeStamp2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var relativePath = Environment.DIRECTORY_DOWNLOADS
            relativePath += File.separator + "Pdf_list"
            val file: File? = getOutputFile(timeStamp, imageFileName)
            outputStream = FileOutputStream(file)
            document.save(outputStream)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            val cr = this.contentResolver
            val uri = cr.insert(Downloads.EXTERNAL_CONTENT_URI, values)
            outputStream = cr.openOutputStream(uri!!)
            uri.let {
                document.save(outputStream)
                document.close()
            }

        } else {
            val file: File? = getOutputFile(timeStamp, imageFileName)
            if (file != null) {
                try {
                    outputStream = FileOutputStream(file)
                    document.save(outputStream)
                    document.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }



    private fun getOutputFile(timeStamp: String, imageFileName: String): File?{
        val root: File = File(getExternalFilesDir(null), "my file")


        var isFolderCreated = true

        if (!root.exists()) {
            isFolderCreated = root.mkdir()
            }

        return if (isFolderCreated) {
            File(root, "$imageFileName.pdf")
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun OpenGallery(view: View) {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    fun NewFile(view: View) {
        imageList.clear()
        mAdapter.updateList(imageList)
        image.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.imageimage))
    }

    fun DeleteImage(view: View) {
        val x = mAdapter.remove(imageList)
        filterlist.remove(filterlist[mAdapter.CP])

        if (x != -1) {
            image.setImageBitmap(imageList[x])
        } else if (imageList.size != 0) {
            image.setImageBitmap(imageList[0])
        } else {
            image.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.imageimage))
        }
        if(imageList.size==0) {
            delete.visibility = GONE
            B_W.visibility = GONE
            level2.visibility = GONE
        }
    }

    fun FilterBitmap(bitmap: Bitmap, value: Int = 128):Bitmap{

        val width = bitmap.width
        val height = bitmap.height
        val xy: Bitmap
        xy = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int
        for (x in 0 until width) {
            for (y in 0 until height) {
                pixel = xy.getPixel(x, y)
                A = Color.alpha(pixel)
                R = Color.red(pixel)
                G = Color.green(pixel)
                B = Color.blue(pixel)
//                var gray = (0.2989 * R + 0.5870 * G + 0.1140 * B).toInt()
//                gray = if (gray > value) {
//                    255
//                } else {
//                    0
//                }
//                xy.setPixel(x, y, Color.argb(A, gray, gray, gray))

                val grayScale = (R * 0.3 + G * 0.59 + B * 0.11).toInt()
                xy.setPixel(x, y, Color.argb(A, grayScale, grayScale, grayScale))
            }
        }
        return xy
    }

    fun FilerClick(view: View) {
        val bitmap = mAdapter.AddFilter(imageList, filterlist)
        image.setImageBitmap(bitmap)
    }

    fun onClickList(view: View) {
        val intent = Intent(this, ShowPdfActivity::class.java)
        startActivity(intent)
    }


}