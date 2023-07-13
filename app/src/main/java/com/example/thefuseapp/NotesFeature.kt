package com.example.thefuseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thefuseapp.databinding.ActivityNotesFeatureBinding

class NotesFeature : AppCompatActivity() {
    private lateinit var edTitle: EditText
    private lateinit var edContent: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button
    private lateinit var btnEdit: Button
    private lateinit var binding: ActivityNotesFeatureBinding

    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter:NoteAdapter? = null
    private var nm:NoteModel? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sqliteHelper = SQLiteHelper(this )
        initRecycleView()
        binding.btnAdd.setOnClickListener{addNote()}
        binding.btnView.setOnClickListener{getNotes()}
        binding.btnEdit.setOnClickListener{updateNote()}
        adapter?.setOnClickitem{
            Toast.makeText(this, it.title, Toast.LENGTH_SHORT).show()
            binding.edTitle.setText(it.title)
            binding.edContent.setText(it.content)
            nm = it
        }
        adapter?.setOnClickDeleteItem { deleteNote(it.id) }
    }

    private fun getNotes(){
        val nmList = sqliteHelper.getAllNotes()
        Log.e("pppp","${nmList.size}")
        adapter?.addItems(nmList)

    }

    private fun addNote(){
        val title = binding.edTitle.text.toString()
        val content = binding.edContent.text.toString()

        if(title.isEmpty() || content.isEmpty()){
            Toast.makeText(this, "Please enter required field", Toast.LENGTH_SHORT).show()
        }else{
            val std = NoteModel(title = title, content = content)
            val status = sqliteHelper.insertNote(std)

            if(status> -1){
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                clearEditText()
                getNotes()
            }else{
                Toast.makeText(this, "Note not added", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateNote(){
        val title = binding.edTitle.text.toString()
        val content= binding.edContent.text.toString()

        if(title == nm?.title && content == nm?.content){
            Toast.makeText(this, "Record not changed", Toast.LENGTH_SHORT).show()
            return
        }
        if(nm==null) return
        val std = NoteModel(id = nm!!.id, title = title, content=content )
        val status = sqliteHelper.updateNote(std)
        if (status > -1){
            clearEditText()
            getNotes()
        }else{
            Toast.makeText(this, "edit failed", Toast.LENGTH_SHORT).show()
        }


    }
    private fun deleteNote(id:Int){
        val builder = AlertDialog.Builder(this )
        builder.setMessage(" Are you sure you want to delete this note")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes" ){
                dialog,_->
            sqliteHelper.deleteNoteID(id)
            getNotes()
            dialog.dismiss()
        }

        builder.setNegativeButton("No" ){
                dialog,_->

            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }
    private fun clearEditText(){
        binding.edTitle.setText("")
        binding.edContent.setText("")
        binding.edTitle.requestFocus()
    }
    private fun initRecycleView(){
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        adapter = NoteAdapter()
        binding.recycleView.adapter = adapter
    }
    private fun initView(){
        edTitle = findViewById(R.id.edTitle)
        edContent = findViewById(R.id.edContent)
        btnAdd= findViewById(R.id.btnAdd)
        btnView = findViewById(R.id.btnView)
        btnEdit = findViewById(R.id.btnEdit)
        recyclerView = findViewById(R.id.recycleView)
    }
}