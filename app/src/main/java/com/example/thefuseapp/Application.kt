package com.example.thefuseapp

import android.app.Application
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.sql.Timestamp
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

class TheApplication: Application() {

    override fun onCreate(){
        super.onCreate()
    }
    companion object{
        fun TimestampFormate(timestamp: Long):String{
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            return android.text.format.DateFormat.format("dd/MM/yyyy",calendar).toString()
        }
        fun loadPDFsize(pdfUrl: String ,pdfTitle:String){
            val TAG = "PDF_SIZE_Tag"
            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            reference.metadata.addOnSuccessListener { storageMeta ->
            val bytes = storageMeta.sizeBytes.toDouble()


            }.addOnFailureListener {n->
                Log.d(TAG,"loadpdfSize ${n.message.toString()}")
            }
        }
        fun loadPage(pdfUrl: String,pdfTitle: String){

            val TAG = "PDF_SIZE_Tag"
            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            reference.getBytes(50000000).addOnSuccessListener { storageMeta ->

            }.addOnFailureListener {n->
                Log.d(TAG,"loadpdfSize ${n.message.toString()}")
            }
        }
        fun loadCategory(categoryId: String, CategoryTV:TextView){
            val reference = FirebaseDatabase.getInstance().getReference("Categories")
            reference.child(categoryId).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val category = "${snapshot.child("category").value}"
                    CategoryTV.text = category
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

}