package com.example.jpegtopdf

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PdfDao {
    @Insert
    suspend fun insert(pdf:Pdf)
    @Delete
    suspend fun delete(pdf: Pdf)

    @Query("Select * from Pdftable order by id ASC")
    fun getAllPdfs(): LiveData<List<Pdf>>

}