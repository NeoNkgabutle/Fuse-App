package com.example.thefuseapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.thefuseapp.databinding.ActivityVisualFormatBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VisualFormat : AppCompatActivity() {
    private lateinit var binding: ActivityVisualFormatBinding

    private lateinit var progressDialog: ProgressDialog
    var Hoursworked = ArrayList<Float>()
    var HoursNames = ArrayList<String>()
    lateinit var toggles: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualFormatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        ReadData()
        toggles = ActionBarDrawerToggle(this@VisualFormat, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggles)
        toggles.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navViews.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.timer -> {
                    val AddTimesheet = Intent(this, DailyGoals::class.java)
                    startActivity(AddTimesheet)
                }
                R.id.calender -> Toast.makeText(applicationContext,"cghj",Toast.LENGTH_SHORT).show()
                R.id.invoice -> Toast.makeText(applicationContext,"cghj",Toast.LENGTH_SHORT).show()
                R.id.graph -> {
                    val graph = Intent(this, DailyGoalGraph::class.java)
                    startActivity(graph)
                }
                R.id.logout -> Toast.makeText(applicationContext,"cghj",Toast.LENGTH_SHORT).show()
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
        reference.child(userID.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                var minGoal = snapshot.child("minDailyGoal").value.toString()
                var maxGoal = snapshot.child("maxDailyGoal").value.toString()

                binding.LineChart.apply {
                    description .isEnabled = false
                    setTouchEnabled(true)
                    setDrawGridBackground(false)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisLeft.setDrawGridLines(false)
                    axisRight.setDrawGridLines(false)
                }
                Hoursworked = ArrayList()
                HoursNames = ArrayList()
                val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Hoursworked.clear()
                        HoursNames.clear()
                        for (i in snapshot.children){
                            var Names = i.child("Name").value.toString()
                            var workedHours = i.child("hoursWorked").value.toString()
                            Hoursworked.add(workedHours.toFloat())
                            HoursNames.add(Names)
                        }
                        val entries = Hoursworked.mapIndexed { index, hoursworked ->
                            Entry(index.toFloat(),hoursworked)
                        }
                        val dataSet = LineDataSet(entries,"Hours worked")
                        dataSet.lineWidth = 2f
                        dataSet.setDrawCircles(true)
                        dataSet.setDrawCircleHole(false)
                        dataSet.setDrawValues(false)
                        val data = LineData(dataSet)
                        val goalDataSet = LineDataSet(null,"Range")
                        goalDataSet.setDrawFilled(true)
                        goalDataSet.setDrawCircles(false)
                        goalDataSet.setDrawCircleHole(false)
                        goalDataSet.setDrawValues(false)
                        goalDataSet.addEntry(Entry(0f,minGoal.toFloat()))
                        goalDataSet.addEntry(Entry(Hoursworked.size.toFloat() -1,minGoal.toFloat()))
                        goalDataSet.addEntry(Entry(Hoursworked.size.toFloat() -1,maxGoal.toFloat()))
                        goalDataSet.addEntry(Entry(0f,maxGoal.toFloat()))

                        val goalData = LineData(goalDataSet)

                        binding.LineChart.data = goalData
                        binding.LineChart.data.addDataSet(dataSet)
                        binding.LineChart.invalidate()
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
}