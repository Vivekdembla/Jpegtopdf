package com.example.jpegtopdf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PdfTable")
data class Pdf(@ColumnInfo val name:String,@ColumnInfo val data:String){
    @PrimaryKey(autoGenerate = true) var id = 0
}
