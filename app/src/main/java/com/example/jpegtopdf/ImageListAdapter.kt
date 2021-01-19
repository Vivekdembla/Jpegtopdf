package com.example.jpegtopdf

import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class ImageListAdapter(private val listener: imageIconClicked): RecyclerView.Adapter<ImageViewHolder>() {
    var CP = 0
    var filtercount=0
    var selectedPos = -1
    private var items: ArrayList<Bitmap> = ArrayList()

    fun remove(imageList: ArrayList<Bitmap>):Int {
        items.remove(items[CP])
        imageList.remove(imageList[CP])
        notifyDataSetChanged()
        return CP-1
    }

    fun AddFilter(imageList: ArrayList<Bitmap>, filterList:ArrayList<Bitmap>):Bitmap{

        val x= imageList[CP]
        imageList.remove(imageList[CP])
        imageList.add(CP,filterList[CP])
        filterList.remove(filterList[CP])
        filterList.add(CP,x)
        updateList(imageList)
        return imageList[CP]



//        val width: Int
//        val height: Int
//        height = imageList[CP].getHeight()
//        width = imageList[CP].getWidth()
//        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val c = Canvas(bmpGrayscale)
//        val paint = Paint()
//        val cm = ColorMatrix()
//        cm.setSaturation(0f)
//        val f = ColorMatrixColorFilter(cm)
//        paint.setColorFilter(f)
//        c.drawBitmap(bmpGrayscale, 0f, 0f, paint)
//        imageList.remove(imageList[CP])
//        imageList.add(CP,bmpGrayscale)
//        items.remove(items[CP])
//        items.add(CP,bmpGrayscale)

    }

    fun addAll(list: List<Bitmap>) {
        items.addAll(list)
        notifyDataSetChanged()
    }
    fun refresh(){
        selectedPos=items.size-1
        CP = selectedPos
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.image_icon, parent, false)
        val viewHolder=ImageViewHolder(view)
        view.setOnClickListener{

            listener.onIconClicked(items[viewHolder.adapterPosition])
            CP=viewHolder.adapterPosition
            selectedPos = CP
            //filtercount = 0
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem=items[position]
        holder.imageIcon.setImageBitmap(currentItem)
        //CP=position
        holder.imageIcon.setBackgroundResource(if (selectedPos == position) R.drawable.back else 0)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateList(updatedImage: ArrayList<Bitmap>) {
        items.clear()
        items.addAll(updatedImage)
        notifyDataSetChanged()

    }

}
class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val imageIcon:ImageView= itemView.findViewById(R.id.imageIcon)
}

interface imageIconClicked{
    fun onIconClicked(item: Bitmap)

}