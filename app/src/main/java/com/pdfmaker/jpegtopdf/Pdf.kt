package com.pdfmaker.jpegtopdf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PdfTable")
data class Pdf(@ColumnInfo val name:String,@ColumnInfo var data:String,@PrimaryKey var id : Int){

}
