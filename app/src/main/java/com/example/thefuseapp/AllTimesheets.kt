package com.example.thefuseapp

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.thefuseapp.databinding.ActivityAllTimesheetsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllTimesheets : AppCompatActivity() {
    private lateinit var binding: ActivityAllTimesheetsBinding
    private lateinit var pdfAdpater: AdapterPDF
    private lateinit var progressDialog: ProgressDialog
    val calendar = Calendar.getInstance()
    public var Image : String? =""
    lateinit var startDate :String
    lateinit var EndDate :String
    private lateinit var pdfArrayList : ArrayList<ModelPdf>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllTimesheetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        val TAG = "FILTER"

        ShowAllTimesheets()
        binding.buttonSearch.setOnClickListener {
            FilterAllTimesheets()
        }
        val SetStartDate = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val Myformat = "YYYY-MM-dd"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.StartDateFilter.setText(sdf.format(calendar.time))
                startDate = binding.StartDateFilter.text.toString()
            }
        }
        binding.StartDateFilter.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@AllTimesheets,
                    SetStartDate,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
        val SetEndDate = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val Myformat = "YYYY-MM-dd"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.EndDateFilter.setText(sdf.format(calendar.time))
                EndDate = binding.EndDateFilter.text.toString()
            }
        }
        binding.EndDateFilter.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@AllTimesheets,
                    SetEndDate,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
    }
    private fun ShowAllTimesheets(){
        progressDialog.show()
        pdfArrayList = ArrayList()

        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
        reference.orderByChild("uid").equalTo(userID).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                for(i in snapshot.children){
                    val model = i.getValue(ModelPdf::class.java)
                    pdfArrayList.add(model!!)
                }

                pdfAdpater = AdapterPDF(this@AllTimesheets,pdfArrayList)
                binding.Alltimesheets.adapter = pdfAdpater
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun FilterAllTimesheets(){
        progressDialog.show()
        pdfArrayList = ArrayList()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val dateformat = SimpleDateFormat("YYYY-MM-dd",Locale.getDefault())
        val StartRange:Date = dateformat.parse(startDate)
        val EndRange : Date = dateformat.parse(EndDate)

        val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
        reference.orderByChild("uid").equalTo(userID).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children) {
                    val model = i.getValue(ModelPdf::class.java)
                    pdfArrayList.add(model!!)
                }
                val filteredList = ArrayList<ModelPdf>()
                filteredList.clear()
                for (project in pdfArrayList){
                    val projectDatestring : String = project.Date
                    val projectDate :Date = dateformat.parse(projectDatestring)
                    if(StartRange>= projectDate && projectDate <= EndRange){
                        filteredList.add(project)
                    }
                }
                progressDialog.dismiss()
                pdfAdpater = AdapterPDF(this@AllTimesheets,filteredList)
                binding.Alltimesheets.adapter = pdfAdpater
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}