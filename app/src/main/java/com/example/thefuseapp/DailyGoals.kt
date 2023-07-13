package com.example.thefuseapp

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.thefuseapp.databinding.ActivityDailyGoalsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyGoals : AppCompatActivity() {
    private lateinit var binding:ActivityDailyGoalsBinding
    private var minDailyGoal = ""
    private var maxDailyGoal = ""
    lateinit var toggles: ActionBarDrawerToggle
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.HoursworkedBtn.setOnClickListener {
            validateWorkDaydata()
        }


        toggles = ActionBarDrawerToggle(this@DailyGoals, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggles)
        toggles.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.timer -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.calender -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.invoice -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
                R.id.graph -> Toast.makeText(applicationContext,"cghj", Toast.LENGTH_SHORT).show()
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

    private fun savetoDataBase() {
        progressDialog.show()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String,Any>()
        hashMap["uid"] = "$userID"
        hashMap["id"] = "$timestamp"
        hashMap["minDailyGoal"] = minDailyGoal
        hashMap["maxDailyGoal"] = maxDailyGoal
        hashMap["timestamp"] = timestamp

        val reference = FirebaseDatabase.getInstance().getReference("DailyGoal")
        reference.child(userID.toString()).setValue(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this,"Daily Goal Logged", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { n->
            progressDialog.dismiss()
            Toast.makeText(this,n.message.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    private fun validateWorkDaydata() {
        minDailyGoal = binding.minimumTime.text.toString().trim()
        maxDailyGoal = binding.maximumTime.text.toString().trim()
        if(minDailyGoal.isEmpty()){
            Toast.makeText(this,"Enter the Minimum Daily Goal",Toast.LENGTH_SHORT).show()
        }else if(maxDailyGoal.isEmpty()){
            Toast.makeText(this,"Enter the Maximum Daily Goal",Toast.LENGTH_SHORT).show()
        }else{
            savetoDataBase()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggles.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}