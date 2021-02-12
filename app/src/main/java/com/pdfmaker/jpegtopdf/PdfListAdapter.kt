package com.pdfmaker.jpegtopdf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PdfListAdapter(private val listener: IconClicked):RecyclerView.Adapter<PdfViewHolder>(){

    val PdfList = ArrayList<Pdf>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.pdf_icon, parent, false)
        val viewHolder = PdfViewHolder(view)
        view.setOnClickListener{
            listener.onIconClicked(PdfList[viewHolder.adapterPosition])
        }
        viewHolder.shareButton.setOnClickListener{
            listener.onShareClick(PdfList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.name.text = PdfList[position].name
        holder.dateTime.text = PdfList[position].data
    }

    override fun getItemCount(): Int {
        return PdfList.size
    }

    fun UpdateList(list:List<Pdf>){
        PdfList.clear()
        PdfList.addAll(list)
        notifyDataSetChanged()
    }

}

interface IconClicked{
    fun onIconClicked(pdf:Pdf)
    fun onShareClick(pdf:Pdf)
}


class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.name)
    val dateTime = itemView.findViewById<TextView>(R.id.datetime)
    val shareButton = itemView.findViewById<Button>(R.id.shareButton)
}