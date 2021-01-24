package com.example.jpegtopdf

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PdfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdf:Pdf)
    @Delete
    suspend fun delete(pdf: Pdf)

    @Query("Select * from Pdftable order by id ASC")
    fun getAllPdfs(): LiveData<List<Pdf>>

}