package com.example.jpegtopdf

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity(), imageIconClicked {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.Top)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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