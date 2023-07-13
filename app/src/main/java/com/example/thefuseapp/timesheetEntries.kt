package com.example.thefuseapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.example.thefuseapp.databinding.ActivityTimesheetEntriesBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.lang.Exception
import java.time.Duration
import java.time.LocalTime

class timesheetEntries : AppCompatActivity() {
    private lateinit var binding : ActivityTimesheetEntriesBinding
    private lateinit var categoryList: ArrayList<ModelCategory>
    private lateinit var TimesheetList : ArrayList<ModelPdf>
    private lateinit var adapterCategory: CategoryAdapter
    val TAG = "Search"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimesheetEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showCategories()
        binding.ButtonADD.setOnClickListener {
            val category = Intent(this,MainActivity::class.java)
            startActivity(category)
        }
        binding.SearchS.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try{
                    adapterCategory.filter.filter(s)
                }catch (e: Exception){
                    Log.d(TAG,"THE SEARCH NOT WORKING DUE ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun showCategories() {
        categoryList = ArrayList()
        TimesheetList = ArrayList()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val referenceTime = FirebaseDatabase.getInstance().getReference("Timesheet")
        referenceTime.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val models = i.getValue(ModelPdf::class.java)
                    TimesheetList.add(models!!)
                }
                val reference = FirebaseDatabase.getInstance().getReference("Categories")
                reference.orderByChild("uid").equalTo(userID).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                            val model = ds.getValue(ModelCategory::class.java)
                            categoryList.add(model!!)
                        }
                        val categoryMap = mutableMapOf<String,Double>()
                        for (timesheets in TimesheetList){
                            val hoursWorked = timesheets.hoursWorked.toDouble()
                            val categoryId = timesheets.categoryId
                            val totalHours = categoryMap.getOrDefault(categoryId,0.0)
                            categoryMap[categoryId] = totalHours + hoursWorked

                            for (category in categoryList){
                                val catId = category.id
                                val total = categoryMap[catId]?:0.0
                                category.hoursCategory = total.toString()

                            }

                        }
                        adapterCategory = CategoryAdapter(this@timesheetEntries,categoryList)
                        binding.CategoryRec.adapter = adapterCategory

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        /*val reference = FirebaseDatabase.getInstance().getReference("Categories")
        reference.orderByChild("uid").equalTo(userID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelCategory::class.java)
                        categoryList.add(model!!)
                    }
                    adapterCategory = CategoryAdapter(this@timesheetEntries,categoryList)
                    binding.CategoryRec.adapter = adapterCategory


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })*/
    }
}