package com.pdfmaker.jpegtopdf

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pdf::class],version = 1,exportSchema = false)
abstract class PdfDataBase:RoomDatabase() {
    abstract fun getPdfDao(): PdfDao

    companion object {

        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile

        private var INSTANCE: PdfDataBase? = null

        fun getDatabase(context: Context): PdfDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PdfDataBase::class.java,
                    "pdf_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance

            }
        }
    }

}