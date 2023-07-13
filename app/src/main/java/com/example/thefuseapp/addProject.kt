package com.example.thefuseapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import com.example.thefuseapp.databinding.ActivityAddProjectBinding
import com.google.android.gms.tasks.Task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.Base64
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class addProject : AppCompatActivity() {
    val calendar = Calendar.getInstance()
    val cal = Calendar.getInstance()
    private lateinit var binding: ActivityAddProjectBinding
    lateinit var toggles: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private  var pdfUri: Uri? = null
    private var ImageUri: Uri? = null
    private val CAPTURE_CODE = 1001
    private val PERMISSION_CODE = 1000
    private var  IURI : Uri? = null
    private val TAG = "PDF_ADD_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading..")
        progressDialog.setCanceledOnTouchOutside(false)
        loadCategories()
        binding.buttonImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission,PERMISSION_CODE)
                }else{
                    takePicture()
                }
            }else{
                takePicture()
            }

        }


        toggles = ActionBarDrawerToggle(this@addProject, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggles)
        toggles.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navViews.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.timer -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
                R.id.calender -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT)
                    .show()

                R.id.invoice -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT)
                    .show()

                R.id.graph -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
                R.id.logout -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
                R.id.category -> {
                    val category = Intent(this, timesheetEntries::class.java)
                    startActivity(category)
                }
            }
            true
        }

        /*This code was derived from YouTube
        * Ep.23 Date Picker + Beautiful EditText (in Kotlin)
        * https://youtu.be/QC9UhvyI18c
        * Andy Al
        * https://www.youtube.com/@andyal868 */
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val Myformat = "YYYY-MM-dd"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.ProjectDate.setText(sdf.format(calendar.time))
            }
        }
        binding.ProjectDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@addProject,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set((Calendar.MINUTE), minute)
                val Myformat = "HH:mm"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.ProjStartTime.setText(sdf.format(cal.time))

            }
        }
        val timeSetListeners = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set((Calendar.MINUTE), minute)
                val Myformat = "HH:mm"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.ProjEndTime.setText(sdf.format(cal.time))
            }
        }
        binding.ProjStartTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                TimePickerDialog(
                    this@addProject, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                ).show()
            }
        })
        binding.ProjEndTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                TimePickerDialog(
                    this@addProject, timeSetListeners,
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                ).show()
            }
        })

        binding.categoryPick.setOnClickListener{
            categoryPick()
        }
        binding.buttonProject.setOnClickListener {
            ValidateandSave()
            createPDF()
        }
        binding.PickPDF.setOnClickListener {
            projectPickIntent()
        }

    }
    private var Name =""
    private var Description =""
    private var categories =""
    private var Date =""
    private var StartTime =""
    private var EndTime =""
    private var Location =""
    private var Cost =""
    private fun ValidateandSave() {
        Log.d(TAG,"Validate and Save: validating data")

        Name = binding.ProjectName.text.toString().trim()
        Description = binding.ProjectDescription.text.toString().trim()
        categories = binding.categoryPick.text.toString().trim()
        Date = binding.ProjectDate.text.toString().trim()
        StartTime = binding.ProjStartTime.text.toString().trim()
        EndTime = binding.ProjEndTime.text.toString().trim()
        Location = binding.ProjectLocation.text.toString().trim()
        Cost = binding.ProjectRate.text.toString().trim()

        if (Name.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Name/Title",Toast.LENGTH_SHORT).show()
        }else if(Description.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Description",Toast.LENGTH_SHORT).show()
        }else if(categories.isEmpty()){
            Toast.makeText(this,"Select a Timesheet Category",Toast.LENGTH_SHORT).show()
        }else if(Date.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Date",Toast.LENGTH_SHORT).show()
        }else if(StartTime.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Start Time",Toast.LENGTH_SHORT).show()
        }else if(EndTime.isEmpty()){
            Toast.makeText(this,"Enter Timesheet End Time",Toast.LENGTH_SHORT).show()
        }else if(Location.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Location",Toast.LENGTH_SHORT).show()
        }else if(Cost.isEmpty()){
            Toast.makeText(this,"Enter Timesheet Hourly Rate",Toast.LENGTH_SHORT).show()
        }else if (pdfUri == null){
            Toast.makeText(this,"Click the green button and pick a pdf",Toast.LENGTH_SHORT).show()
        }else if (StartTime >= EndTime){
            Toast.makeText(this,"Start Time cannot be greater than End Time",Toast.LENGTH_SHORT).show()
        }else{
            uploadProject()
        }
    }


    private fun uploadProject() {
        Log.d(TAG,"UploadProject: Uploading to storage")

        progressDialog.setMessage("Uploading Timesheet")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()
        val filePathAndName = "Timesheet/$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!).addOnSuccessListener {N->
            Log.d(TAG,"uploadProject: Uploading complete ")
            progressDialog.dismiss()
            val uriTask : Task<Uri> = N.storage.downloadUrl
            while(!uriTask.isSuccessful);
            val uploadedPDF = "${uriTask.result}"
            uploadtoDatabase(uploadedPDF,timestamp)
        }.addOnFailureListener{N->
            Log.d(TAG,"uploadProject: Uploading to storage failed ${N.message}")
            progressDialog.dismiss()
            Toast.makeText(this,"Upload Failed ${N.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadtoDatabase(uploadedPDF: String, timestamp: Long) {
        Log.d(TAG,"uploadToDatabase: Uploading to database ")

        var start : LocalTime = LocalTime.parse(StartTime)
        var end : LocalTime = LocalTime.parse(EndTime)
        var duration : Duration = Duration.between(start,end)
        var HoursWorked : Float = duration.toHours().toFloat()
        var HourWorkedDouble : Double = duration.toHours().toDouble()
        var Totalcost = Cost.toDouble() * HourWorkedDouble
        var HoursWorkedString : String = HoursWorked.toString()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val hashMap: HashMap<String,Any> = HashMap()
        hashMap["uid"]= "$userID"
        hashMap["id"]= "$timestamp"
        hashMap["Name"]= Name
        hashMap["Description"]= Description
        hashMap["categoryId"]= selectedCategoryID
        hashMap["Date"]= Date
        hashMap["StartTime"]= StartTime
        hashMap["EndTime"]= EndTime
        hashMap["Location"]= Location
        hashMap["Cost"]= "$Totalcost"
        hashMap["hoursWorked"] = HoursWorkedString
        hashMap["url"]= "$uploadedPDF"
        hashMap["TimeImage"]= "$ImageUri"
        hashMap["timestamp"]= timestamp
        hashMap["viewsCount"]= 0
        hashMap["downloadsCount"]= 0

        val reference = FirebaseDatabase.getInstance().getReference("Timesheet")
        reference.child("$timestamp").setValue(hashMap).addOnSuccessListener {
            Log.d(TAG,"uploadToDatabase: Uploaded to database ")
            Toast.makeText(this, "Uploaded",Toast.LENGTH_SHORT).show()
            pdfUri = null

        }.addOnFailureListener {N->
            Log.d(TAG,"uploadToDatabase: Uploading to database failed ${N.message} ")
            Toast.makeText(this,"Failed to upload ${N.message}",Toast.LENGTH_SHORT).show()
        }


    }

    private fun loadCategories(){
        Log.d(TAG,"loadCategories : Loading Timesheet categories")
        progressDialog.setMessage("Loading Categories")
        progressDialog.show()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        categoryArrayList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Categories")
        reference.orderByChild("uid").equalTo(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                categoryArrayList.clear()
                for (N in snapshot.children){
                    val model = N.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.category}")
                }
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun createPDF() {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME,"${binding.ProjectName.text.toString().trim()}")
        values.put(MediaStore.MediaColumns.MIME_TYPE,"application/pdf")
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        val uri :Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"),values)
        try{
            if (uri != null){
                val outputStream = contentResolver.openOutputStream(uri)
                val document = com.itextpdf.text.Document()
                PdfWriter.getInstance(document,outputStream)
                document.open()
                document.addAuthor("Author")
                pdfInfo(document)
                document.close()
                Toast.makeText(this,"PdF is created",Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){
            Toast.makeText(this,"${e.message.toString()}",Toast.LENGTH_SHORT).show()
        }

    }
    fun pdfInfo(document: com.itextpdf.text.Document){

        var start : LocalTime = LocalTime.parse(StartTime)
        var end : LocalTime = LocalTime.parse(EndTime)
        var duration : Duration = Duration.between(start,end)
        var HourWorkedDouble : Double = duration.toHours().toDouble()
        var Totalcost = Cost.toDouble() * HourWorkedDouble
        val paragraph = Paragraph()
        val heading = Font(Font.FontFamily.HELVETICA,50f,Font.BOLDITALIC)
        paragraph.add(Paragraph("FuseTime\nTimesheet\nInvoice",heading))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Name : ${binding.ProjectName.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Description : ${binding.ProjectDescription.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Category : ${binding.categoryPick.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Date : ${binding.ProjectDate.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet StartTime : ${binding.ProjStartTime.text.toString().trim()} & EndTime : ${binding.ProjEndTime.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Hours : ${HourWorkedDouble.toString()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Location : ${binding.ProjectLocation.text.toString().trim()}"))
        addSpaces(paragraph,1)
        paragraph.add(Paragraph("Timesheet Cost : $Totalcost"))
        addSpaces(paragraph,1)
        document.add(paragraph)
    }
    fun addSpaces(paragraph: Paragraph,lineCount:Int){
        for (i in 0 until lineCount){
            paragraph.add(Paragraph("----------------------------------------------"))
        }
    }
    private var selectedCategoryID = ""
    private var selectedCategoryTitle = ""
    private fun categoryPick(){
        progressDialog.show()
        Log.d(TAG,"categoryPick: Showing Timesheet category")
        val categoryArray = arrayOfNulls<String>(categoryArrayList.size)
        for (N in categoryArrayList.indices){
            categoryArray[N] = categoryArrayList[N].category
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick a Category").setItems(categoryArray){ dialog, which ->
            selectedCategoryTitle = categoryArrayList[which].category
            selectedCategoryID = categoryArrayList[which].id
            binding.categoryPick.text = selectedCategoryTitle
            Log.d(TAG,"categoryPick: Selected Category ID: $selectedCategoryID")
            Log.d(TAG,"categoryPick: Selected Category Title $selectedCategoryTitle")
        }.show()
        progressDialog.dismiss()
    }
    private fun projectPickIntent(){
        Log.d(TAG,"projectPickIntent: Stating Project pick intent")

        val ProjectIntent = Intent()
        ProjectIntent.type = "application/pdf"
        ProjectIntent.action = Intent.ACTION_GET_CONTENT
        projectActivityResultLauncher.launch(ProjectIntent)
    }
    val projectActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {result ->
        if(result.resultCode == RESULT_OK){
            Log.d(TAG," Project picked")
            pdfUri = result.data!!.data
        }else{
            Log.d(TAG,"Project picked cancelled")
            Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
        }
        }
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE ->{
                if (grantResults.size > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    takePicture()
                }else{
                    Toast.makeText(this, "Permission was denied",Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun takePicture(){
        val cameraIn = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIn.putExtra(MediaStore.EXTRA_OUTPUT,CAPTURE_CODE)
        startActivityForResult(cameraIn,CAPTURE_CODE)
    }
    private fun uploadImage(bitmap: Bitmap){
        progressDialog.setMessage("Upload Image")
        progressDialog.show()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val timestamp = System.currentTimeMillis()
        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("$timestamp.jpg")

        // Create a reference to 'images/mountains.jpg'
        val ImagesRef = storageRef.child("images/$timestamp.jpg")

        // While the file names are the same, the references point to different files
        mountainsRef.name == ImagesRef.name // true
        mountainsRef.path == ImagesRef.path // false


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()

        val uploadTask = ImagesRef.putBytes(data).continueWithTask { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    throw it
                }
            }
            ImagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                ImageUri = task.result
            } else {

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_CODE){

            val imageBitmap = data?.extras?.get("data")as Bitmap
            uploadImage(imageBitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggles.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}




