package com.example.jpegtopdf

import android.R.attr.src
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.spongycastle.dvcs.CPDRequestBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), imageIconClicked {
    var filtercount=1
    lateinit var B_W:Button
    lateinit var delete:Button
    var COUNT=1
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Capture=findViewById(R.id.Capture)
        PickFromGallery= findViewById(R.id.PickFromGallery)
        image = findViewById(R.id.image)
        delete = findViewById(R.id.Delete)
        B_W = findViewById(R.id.B_W)
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapter= ImageListAdapter(this)
        recyclerView.adapter=mAdapter


    }

    fun onClickingCapture(view: View) {
        dispatchTakePictureIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic()
            delete.visibility= VISIBLE
            B_W.visibility = VISIBLE
            mAdapter.refresh()
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data
            image.setImageURI(imageUri)
            val bitmap = (image.drawable as BitmapDrawable).bitmap
            val scaled = Bitmap.createScaledBitmap(bitmap, 600, 800, false)
            if(mAdapter.CP<imageList.size) {
                imageList.add(mAdapter.CP + 1, scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(mAdapter.CP+1,x)
            }
            else{
                imageList.add(scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(x)
            }
            mAdapter.updateList(imageList)
            delete.visibility= VISIBLE
            B_W.visibility = VISIBLE
            mAdapter.refresh()
        }
    }

    private fun setPic() {
        val targetW: Int = image.width
        val targetH: Int = image.height

        val bmOptions = BitmapFactory.Options().apply {

            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            image.setImageBitmap(bitmap)
            val scaled = Bitmap.createScaledBitmap(bitmap, 600, 800, false)
            if(mAdapter.CP<imageList.size) {
                imageList.add(mAdapter.CP + 1, scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(mAdapter.CP+1,x)
            }
            else{
                imageList.add(scaled)
                val x = FilterBitmap(scaled)
                filterlist.add(x)
            }
        }
        mAdapter.updateList(imageList)

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
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(this, "File Path: ${Environment.DIRECTORY_DOWNLOADS}/Pdf list", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "File Path: ${getExternalFilesDir(null)}/my file", Toast.LENGTH_SHORT).show()
        }
    }




    private fun CreatePdf(list: ArrayList<Bitmap>){
        val document = PDDocument()
        for (i in 0 until list.size) {
            val page = PDPage(A4)
            document.addPage(page)
            val contentStream = PDPageContentStream(document, page)
            val ximage = JPEGFactory.createFromImage(document, list[i], 1f)

            contentStream.drawImage(ximage, 0f, 0f)

            contentStream.close()
        }
        val outputStream: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var relativePath = Environment.DIRECTORY_DOWNLOADS
            relativePath += File.separator + "Pdf list"
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "PDF_$timeStamp"
            val fileName: String = "$imageFileName.pdf"
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            val cr = this.contentResolver
            val uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            outputStream = cr.openOutputStream(uri!!)
            uri.let {

                document.save(outputStream)
                document.close()
            }
        } else {
            val file: File? = getOutputFile()
            if (file != null)
                try {
                    outputStream = FileOutputStream(file)
                    document.save(outputStream)
                    document.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
        }

    }


    private fun getOutputFile(): File?{
        val root: File = File(getExternalFilesDir(null), "my file")


        var isFolderCreated = true

        if (!root.exists()) {
            isFolderCreated = root.mkdir()
            }

        return if (isFolderCreated) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "PDF_$timeStamp"
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

    fun onAddClick(view: View) {
        COUNT++
        if (COUNT % 2 == 0) {
            PickFromGallery.visibility = VISIBLE
            Capture.visibility = VISIBLE
        } else {
            PickFromGallery.visibility = GONE
            Capture.visibility = GONE
        }
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
        }
    }

    fun FilterBitmap(bitmap: Bitmap):Bitmap{
        val width: Int
        val height: Int
        height = bitmap.getHeight()
        width = bitmap.getWidth()

        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bitmap, 0f, 0f, paint)
        return bmpGrayscale
    }

    fun FilerClick(view: View) {
        val bitmap = mAdapter.AddFilter(imageList,filterlist)
        image.setImageBitmap(bitmap)
    }


}