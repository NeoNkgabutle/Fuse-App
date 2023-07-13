package com.example.thefuseapp

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.thefuseapp.databinding.ActivityListofTimesheetsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListofTimesheets : AppCompatActivity() {
    private lateinit var pdfArrayList : ArrayList<ModelPdf>
    private lateinit var binding: ActivityListofTimesheetsBinding
    private lateinit var pdfAdpater: AdapterPDF
    private lateinit var progressDialog: ProgressDialog
    var categoryId = ""
    var category = ""
    var TAG ="LoadPDF"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListofTimesheetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Timesheets Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!
        loadTimeSheets()
    }
    private fun loadTimeSheets(){
        progressDialog.show()
        pdfArrayList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
       reference.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(object : ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               progressDialog.dismiss()
               pdfArrayList.clear()
               for (i in snapshot.children){
                   val model = i.getValue(ModelPdf::class.java)
                   if(model != null){
                       pdfArrayList.add(model)
                       Log.d(TAG,"onDataChange: ${model.Name} ${model.categoryId}")
                   }
               }
               pdfAdpater = AdapterPDF(this@ListofTimesheets,pdfArrayList)
               binding.TimesheetList.adapter = pdfAdpater

           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

       })
    }
}