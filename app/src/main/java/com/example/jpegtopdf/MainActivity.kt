package com.example.jpegtopdf


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Downloads
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
    private final var TAG = "MainActivity"
    lateinit var blur:ImageView
    lateinit var renameBackground:ImageView
    lateinit var creatingPdf:TextView
    lateinit var fileName:TextView
    lateinit var nameOfFile:EditText
    lateinit var okButton:Button
    lateinit var cancelButton:Button
    lateinit var rotate:Button
    lateinit var New:Button
    lateinit var PdfList:Button
    lateinit var Convert:Button
    lateinit var progressBar:ProgressBar
    lateinit var nameofpdf:String
    lateinit var dateofpdf:String
    lateinit var level2:ImageView
    lateinit var guideline7:Guideline
    lateinit var B_W:Button
    lateinit var recyclerView:RecyclerView
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
    var mInterstitialAd: InterstitialAd?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.Top)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
        blur = findViewById(R.id.blur)
        renameBackground=findViewById(R.id.renameBackground)
        creatingPdf =findViewById(R.id.creatingPdf)
        fileName= findViewById(R.id.fileName)
        nameOfFile=findViewById(R.id.nameOfFile)
        okButton=findViewById(R.id.okButton)
        cancelButton= findViewById(R.id.cancelButton)
        rotate = findViewById(R.id.rotate)
        New = findViewById(R.id.New)
        PdfList = findViewById(R.id.PdfList)
        Convert = findViewById(R.id.Convert)
        progressBar = findViewById(R.id.progressBar)
        level2=findViewById(R.id.level2)
        Capture=findViewById(R.id.Capture)
        PickFromGallery= findViewById(R.id.PickFromGallery)
        image = findViewById(R.id.image)
        delete = findViewById(R.id.Delete)
        B_W = findViewById(R.id.B_W)
        guideline7 = findViewById(R.id.guideline7)
        recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapter= ImageListAdapter(this)
        recyclerView.adapter=mAdapter
        MobileAds.initialize(this) {}
        fetchAd()

    }

    fun fetchAd(){

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3461290911046528/4857489097", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("this", adError.message);
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("this", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("this", "Ad was dismissed.");
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d("this", "Ad failed to show.");
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("this", "Ad showed fullscreen content.");
                mInterstitialAd = null
            }
        }
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
                    mAdapter.CP+=1
                    mAdapter.updateList(imageList)
                    progressBar.visibility = GONE
                    mAdapter.refresh()
                    delete.visibility = VISIBLE
                    Convert.visibility = VISIBLE
                    B_W.visibility = VISIBLE
                    rotate.visibility = VISIBLE
                    level2.visibility = VISIBLE
                    image.setImageBitmap(imageList[mAdapter.CP])

//                    val x = FilterBitmap(imageList[p])
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            progressBar.visibility = VISIBLE
            level2.visibility = VISIBLE
            val imageUri = data?.data
            image.setImageURI(imageUri)
            Log.e("this", "image k andr")
            val y = (image.drawable as BitmapDrawable).bitmap
            val bitmap = y
            Log.e("this", "bitmap k andr")
            GlobalScope.launch(Dispatchers.IO) {
                val scaled: Bitmap
                if (bitmap.height > bitmap.width) {
                    val x: Float = bitmap.height.toFloat() / A2.height
                    scaled = getResizedBitmap(bitmap, (bitmap.width / x).toInt(), A2.height.toInt())
                    Log.e("this", "scaled k andr")

                } else {
                    val x: Float = bitmap.width.toFloat() / A2.width
                    scaled =
                        getResizedBitmap(bitmap, A2.width.toInt(), (bitmap.height / x).toInt())
                    Log.e("this", "else scaled k andr")
                }
                imageList.add(mAdapter.CP + 1, scaled)
                filterlist.add(mAdapter.CP + 1, scaled)
                withContext(Dispatchers.Main) {
                    mAdapter.CP+=1
                    mAdapter.updateList(imageList)
                    delete.visibility = VISIBLE
                    Convert.visibility = VISIBLE
                    B_W.visibility = VISIBLE
                    rotate.visibility = VISIBLE
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
                var scaled:Bitmap
                if (bitmap.height > bitmap.width) {
                    val x: Float = bitmap.height.toFloat() / A2.height
                    scaled = getResizedBitmap(bitmap, (bitmap.width / x).toInt() + 100, A2.height.toInt())
                } else {
                    val x: Float = bitmap.width.toFloat() / A2.width
                    scaled = getResizedBitmap(bitmap, A2.width.toInt(), (bitmap.height / x).toInt() + 100)
                }

                val matrix = Matrix()
                val rotate = getImageOrientation(currentPhotoPath)
                matrix.postRotate(rotate.toFloat())
                scaled = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true)

//                if (mAdapter.CP < imageList.size) {
                imageList.add(mAdapter.CP + 1, scaled)
                filterlist.add(mAdapter.CP + 1, scaled)
//                    p=mAdapter.CP+1
//                val x = FilterBitmap(scaled)
//                filterlist.add(mAdapter.CP + 1, x)
//                } else {
//                    imageList.add(scaled)
//                    p=mAdapter.CP
//                val x = FilterBitmap(scaled)
//                filterlist.add(x)
//                }
            }
    }

    fun getImageOrientation(imagePath: String?): Int {
        var rotate = 0
        try {
            val exif = ExifInterface(imagePath!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotate
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
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            fetchAd()
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            fetchAd()
        }
        renameBackground.visibility= VISIBLE
        creatingPdf.visibility= VISIBLE
        fileName.visibility= VISIBLE
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        nameOfFile.visibility= VISIBLE
        nameOfFile.setText("PDF_$timeStamp")
        okButton.visibility= VISIBLE
        cancelButton.visibility= VISIBLE
        blur.visibility = VISIBLE
        recyclerView.visibility = GONE
        PdfList.visibility = GONE
        Capture.visibility = GONE
        PickFromGallery.visibility = GONE
        B_W.visibility = GONE
        Convert.visibility = GONE
        New.visibility = GONE
        delete.visibility = GONE
        rotate.visibility = GONE
        image.visibility = VISIBLE
        nameOfFile.requestFocus()
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(nameOfFile, InputMethodManager.SHOW_IMPLICIT)

    }

    fun pdfAfterName(view: View){

        GlobalScope.launch(Dispatchers.IO) {
            CreatePdf(imageList, "${nameOfFile.getText()}.pdf")
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

    private fun CreatePdf(list: ArrayList<Bitmap>, name: String){
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
        val timeStamp2: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(Date())
        //val imageFileName = "PDF_$timeStamp"
        val fileName= name
        nameofpdf=fileName
        dateofpdf=timeStamp2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var relativePath = Environment.DIRECTORY_DOWNLOADS
            relativePath += File.separator + "Pdf_list"
            val file: File? = getOutputFile(fileName)
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
            val file: File? = getOutputFile(fileName)
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

    private fun getOutputFile(imageFileName: String): File?{
        val root: File = File(getExternalFilesDir(null), "my file")


        var isFolderCreated = true

        if (!root.exists()) {
            isFolderCreated = root.mkdir()
            }

        return if (isFolderCreated) {
            File(root, "$imageFileName")
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
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            fetchAd()
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            fetchAd()
        }
        imageList.clear()
        mAdapter.updateList(imageList)
        image.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.imageimage))
    }

    fun DeleteImage(view: View) {
        val x = mAdapter.remove(imageList)
        filterlist.remove(filterlist[mAdapter.CP])

        if (x > -1) {
            image.setImageBitmap(imageList[x])
            mAdapter.CP=x
        } else if(x==-1&&imageList.size==0){
            image.setImageDrawable(ActivityCompat.getDrawable(this, R.drawable.imageimage))
            mAdapter.CP=x
        }
        else{
            image.setImageBitmap(imageList[0])
            mAdapter.CP=0
        }
        if(imageList.size==0) {
            Convert.visibility = GONE
            delete.visibility = GONE
            B_W.visibility = GONE
            rotate.visibility = GONE
        }
        mAdapter.updateList(imageList)
        mAdapter.refresh()

    }

    fun FilterBitmap(bitmap: Bitmap):Bitmap{

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
        progressBar.visibility= VISIBLE
        Capture.visibility = GONE
        PickFromGallery.visibility= GONE
        B_W.visibility= GONE
        rotate.visibility = GONE
        PdfList.visibility = GONE
        Convert.visibility = GONE
        New.visibility = GONE
        delete.visibility = GONE
        GlobalScope.launch(Dispatchers.IO) {
            if (filterlist[mAdapter.CP] == imageList[mAdapter.CP]) {
                filterlist.remove(filterlist[mAdapter.CP])
                filterlist.add(mAdapter.CP, FilterBitmap(imageList[mAdapter.CP]))
            }
            withContext(Dispatchers.Main) {
                val bitmap = mAdapter.AddFilter(imageList, filterlist)
                progressBar.visibility = GONE
                Capture.visibility = VISIBLE
                PickFromGallery.visibility= VISIBLE
                B_W.visibility= VISIBLE
                rotate.visibility = VISIBLE
                PdfList.visibility = VISIBLE
                Convert.visibility = VISIBLE
                New.visibility = VISIBLE
                delete.visibility = VISIBLE
                image.setImageBitmap(bitmap)
            }
        }
    }

    fun onClickList(view: View) {
        val intent = Intent(this, ShowPdfActivity::class.java)
        startActivity(intent)
    }

    fun onClickRotate(view: View) {
        val matrix = Matrix()
        matrix.postRotate(90f)
        if(imageList[mAdapter.CP]==filterlist[mAdapter.CP]) {
            imageList[mAdapter.CP] = Bitmap.createBitmap(imageList[mAdapter.CP], 0, 0, imageList[mAdapter.CP].getWidth(), imageList[mAdapter.CP].getHeight(), matrix, true)
            filterlist[mAdapter.CP]=imageList[mAdapter.CP]
        }
        else {
            imageList[mAdapter.CP] = Bitmap.createBitmap(imageList[mAdapter.CP], 0, 0, imageList[mAdapter.CP].getWidth(), imageList[mAdapter.CP].getHeight(), matrix, true)
            filterlist[mAdapter.CP] = Bitmap.createBitmap(filterlist[mAdapter.CP], 0, 0, filterlist[mAdapter.CP].getWidth(), filterlist[mAdapter.CP].getHeight(), matrix, true)

        }
        image.setImageBitmap(imageList[mAdapter.CP])
        mAdapter.updateList(imageList)
    }

    fun onClickCancel(view: View) {
        renameBackground.visibility= GONE
        creatingPdf.visibility= GONE
        fileName.visibility= GONE
        nameOfFile.visibility= GONE
        okButton.visibility= GONE
        cancelButton.visibility= GONE
        blur.visibility = GONE
        recyclerView.visibility = VISIBLE
        PdfList.visibility = VISIBLE
        Capture.visibility = VISIBLE
        PickFromGallery.visibility = VISIBLE
        B_W.visibility = VISIBLE
        Convert.visibility = VISIBLE
        New.visibility = VISIBLE
        delete.visibility = VISIBLE
        rotate.visibility = VISIBLE
    }
}

