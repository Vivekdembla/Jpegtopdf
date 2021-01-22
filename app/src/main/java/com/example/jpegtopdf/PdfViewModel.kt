package com.example.jpegtopdf

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PdfViewModel(application: Application) : AndroidViewModel(application) {

    val allPdfs : LiveData<List<Pdf>>

    private val repository: PdfRepository

    init {
        Log.e("this","Paunch gye viewmodel fun me")
        val dao=PdfDataBase.getDatabase(application).getPdfDao()
        Log.e("this","Paunch gye doa k aage")
        repository= PdfRepository(dao)
        allPdfs = repository.allPdfs
    }
    fun deletePdf(pdf:Pdf) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(pdf)
    }

    fun insertPdf(pdf:Pdf) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(pdf)
    }

}