package com.example.jpegtopdf

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HomeActivity : AppCompatActivity(), imageIconClicked {

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.Top)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val x:ArrayList<Bitmap> = ArrayList<Bitmap>()
        val adapter= ImageListAdapter(this)
        adapter.updateList(x)
    }

    fun onClickNewFile(view: View) {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickOpen(view: View) {
        val intent = Intent(this,ShowPdfActivity::class.java)
        startActivity(intent)
    }

    override fun onIconClicked(item: Bitmap) {

    }
}