package com.example.jpegtopdf

import androidx.lifecycle.LiveData

class PdfRepository(private val pdfDao: PdfDao) {
    val allPdfs: LiveData<List<Pdf>> = pdfDao.getAllPdfs()
    suspend fun insert(pdf: Pdf) {
        pdfDao.insert(pdf)
    }

    suspend fun delete(pdf: Pdf){
        pdfDao.delete(pdf)
    }
}