package com.example.jpegtopdf
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import java.io.File

open class ShowPdfActivity : AppCompatActivity(), IconClicked {
    lateinit var viewModel: PdfViewModel
    private lateinit var mAdapter2: PdfListAdapter
    lateinit var recyclerView2: RecyclerView
    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.Top)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_pdf)
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        recyclerView2 = findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = GridLayoutManager(this, 2)
        mAdapter2 = PdfListAdapter(this)
        recyclerView2.adapter = mAdapter2

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(PdfViewModel::class.java)
        viewModel.allPdfs.observe(this, androidx.lifecycle.Observer { list ->
            list?.let {
                mAdapter2.UpdateList(it)
            }
        })
        val bundle = intent.extras
        if(bundle?.getString("FileName")!=null&&bundle.getString("FileDate")!=null) {
            val PdfToBeAdded2 = Pdf(bundle?.getString("FileName")!!, bundle.getString("FileDate")!!)
            viewModel.insertPdf(PdfToBeAdded2)
        }
    }

    override fun onIconClicked(pdf: Pdf) {
        var x = File(getExternalFilesDir(null), "my file")
        val file = File(x, "${pdf.name}")
        if (file.exists()) {
        val uri = FileProvider.getUriForFile(this, "com.example.jpegtopdf" + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(uri, "application/pdf")
            try {
                this.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("this", "Activity not found")
            }
        }


    }

    override fun onShareClick(pdf: Pdf) {

        var x = File(getExternalFilesDir(null), "my file")
        val file = File(x, "${pdf.name}")
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(this, "com.example.jpegtopdf" + ".provider", file)
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            this.startActivity(share)
        }
    }


    fun onClickingBack(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    fun OnClickHome(view: View) {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}