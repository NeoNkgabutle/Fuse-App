package com.example.thefuseapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.thefuseapp.databinding.ActivityDailyGoalGraphBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DailyGoalGraph : AppCompatActivity() {
    private lateinit var binding:ActivityDailyGoalGraphBinding
    lateinit var toggles: ActionBarDrawerToggle
    private lateinit var progressDialog: ProgressDialog
    var Hoursworked = ArrayList<Float>()
    var HoursNames = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyGoalGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        ReadData()
        toggles = ActionBarDrawerToggle(this@DailyGoalGraph, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggles)
        toggles.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.timer -> {
                    val AddTimesheet = Intent(this, DailyGoals::class.java)
                    startActivity(AddTimesheet)
                }
                R.id.calender -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.invoice -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.graph -> {
                    val graph = Intent(this, DailyGoalGraph::class.java)
                    startActivity(graph)
                }
                R.id.logout -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.Addtimesheet ->{
                    val AddTimesheet = Intent(this, addProject::class.java)
                    startActivity(AddTimesheet)
                }
                R.id.category -> {
                    val category = Intent(this,timesheetEntries::class.java)
                    startActivity(category)
                }
                R.id.timesheet -> {
                    val ListofTime = Intent(this, AllTimesheets::class.java)
                    startActivity(ListofTime)
                }

            }
            true
        }
    }

    private fun ReadData() {
        progressDialog.show()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("DailyGoal")
        reference.child(userID.toString()).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                val minGoal = snapshot.child("minDailyGoal").value.toString()
                val maxGoal = snapshot.child("maxDailyGoal").value.toString()
                val xAxis = binding.BarChart.xAxis
                val yAxis = binding.BarChart.axisLeft
                binding.BarChart.apply {
                    setDrawBarShadow(false)
                    setDrawValueAboveBar(true)
                    isDoubleTapToZoomEnabled = false
                    setPinchZoom(false)
                    setMaxVisibleValueCount(60)
                }
                xAxis.apply {
                    setDrawGridLines(false)
                    granularity = 0.5f
                    setDrawLabels(true)
                    position = XAxis.XAxisPosition.BOTTOM
                }

                yAxis.apply {
                    setDrawGridLines(true)
                    axisMinimum = minGoal.toFloat()
                    axisMaximum = maxGoal.toFloat()
                }
                binding.BarChart.axisRight.isEnabled = false
                Hoursworked = ArrayList()
                HoursNames = ArrayList()
                val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
                reference.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Hoursworked.clear()
                        HoursNames.clear()
                        for (i in snapshot.children){
                            val date = i.child("Date").value.toString()
                            val workedHours = i.child("hoursWorked").value.toString()
                            Hoursworked.add(workedHours.toFloat())
                            HoursNames.add(date)
                        }
                        val entries = Hoursworked.mapIndexed { index, hoursworked ->
                            BarEntry(index.toFloat(),hoursworked)
                        }
                        val dataSet = BarDataSet(entries,"Hours worked")
                        val data = BarData(dataSet)
                        binding.BarChart.xAxis.valueFormatter = IndexAxisValueFormatter(HoursNames)
                        binding.BarChart.data = data
                        binding.BarChart.invalidate()
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
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggles.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}