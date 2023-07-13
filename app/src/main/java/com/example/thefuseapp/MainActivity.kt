package com.example.thefuseapp

import android.app.ProgressDialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment

import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.thefuseapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


import java.io.File
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
     var STORAGE_CODE = 17834
    var filename: String  = "Good.pdf"
    var pageHeight = 1120
    var pageWidth = 720

    lateinit var toggles: ActionBarDrawerToggle
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.categoryButton.setOnClickListener {
            addDataValidate()
        }
        toggles = ActionBarDrawerToggle(this@MainActivity, binding.drawerLayout, 0, 0)
        binding.drawerLayout.addDrawerListener(toggles)
        toggles.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.timer -> {
                    val AddTimesheet = Intent(this, DailyGoals::class.java)
                    startActivity(AddTimesheet)
                }
                R.id.visualFormate ->{
                    val visualFormate = Intent(this, VisualFormat::class.java)
                    startActivity(visualFormate)
                }
                R.id.calender -> {
                    val Calendar = Intent(this,NotesFeature::class.java)
                    startActivity(Calendar)
                }
                R.id.graph -> {
                    val graph = Intent(this, DailyGoalGraph::class.java)
                    startActivity(graph)
                }
                R.id.logout -> {
                    val Logout = Intent(this,loginScreen::class.java)
                    startActivity(Logout)
                }
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

    private fun getFilePath(): String?{
        val contextWrapper = ContextWrapper(applicationContext)
        val documentDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentDirectory,"good"+".pdf")
        return file.path
    }







    fun checkPermission():Boolean{
        var write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        var Manage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        return write == PackageManager.PERMISSION_GRANTED
    }
    fun RequestPermission(){
         ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),STORAGE_CODE)


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // on below line we are checking if the
        // request code is equal to permission code.
        if (requestCode == STORAGE_CODE) {

            // on below line we are checking if result size is > 0
            if (grantResults.size > 0) {

                // on below line we are checking
                // if both the permissions are granted.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    // if permissions are granted we are displaying a toast message.
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {

                    // if permissions are not granted we are
                    // displaying a toast message as permission denied.
                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private  var category =""
    private var hoursCategory = ""
    private fun addDataValidate() {
        category = binding.categoryName.text.toString().trim()

        if (category.isEmpty()){
            Toast.makeText(this,"Enter a Category", Toast.LENGTH_SHORT).show()
        }else{
            addCategorytoFirebase()
        }
    }



    private fun addCategorytoFirebase() {
        progressDialog.show()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val timestamp = System.currentTimeMillis()
        val hashMap  = HashMap<String,Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"]= category
        hashMap["hoursCategory"] = hoursCategory
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "$userID"

        val reference = FirebaseDatabase.getInstance().getReference("Categories")
        reference.child("$timestamp").setValue(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this,"Category Added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { n->
            progressDialog.dismiss()
            Toast.makeText(this,n.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggles.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}