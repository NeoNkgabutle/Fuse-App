package com.example.thefuseapp


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.VERSION

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object{
     private const val DATABASE_NAME ="notes.db"
     private const val DATABASE_VERSION = 1
     private const val TBL_NOTE = "tbl_note"
     private const val ID = "id"
     private const val TITLE = "title"
     private const val CONTENT = "content"

    }

    override fun onCreate(p0: SQLiteDatabase?) {
       val createTblNote = "CREATE TABLE $TBL_NOTE ($ID INTEGER PRIMARY KEY, $TITLE TEXT , $CONTENT TEXT )"
        p0?.execSQL(createTblNote)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
       p0!!.execSQL("DROP TABLE IF EXISTS $TBL_NOTE")
        onCreate(p0)
    }

    fun insertNote(std:NoteModel): Long{
        val p0 = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, std.id)
        contentValues.put(TITLE, std.title)
        contentValues.put(CONTENT, std.content)

        val success = p0.insert(TBL_NOTE, null, contentValues)
        p0.close()
        return success

    }


    fun getAllNotes(): ArrayList<NoteModel>{
        val nmList:ArrayList<NoteModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_NOTE"
        val p0 = this.readableDatabase

        val cursor: Cursor?

        try{
            cursor = p0.rawQuery(selectQuery, null)
        }catch (e: Exception){
            e.printStackTrace()
            p0.execSQL(selectQuery)
            return ArrayList()
        }
          var id: Int
          var title: String
          var content: String


        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                content = cursor.getString(cursor.getColumnIndex("content"))
               val std = NoteModel(id= id, title = title, content = content)
                nmList.add(std)

            }while(cursor.moveToNext())
        }
        cursor.close()
        return nmList
    }
  fun updateNote(nm: NoteModel):Int{
      val p0 = this.writableDatabase
      val contentValues = ContentValues()
      contentValues.put(ID, nm.id)
      contentValues.put(TITLE, nm.title)
      contentValues.put(CONTENT, nm.content)

      val success = p0.update(TBL_NOTE, contentValues, "id=" + nm.id, null )
      p0.close()
      return success


  }
   fun deleteNoteID(id:Int): Int{
       val p0 = this.writableDatabase
       val contentValues = ContentValues()
       contentValues.put(ID, id)

       val success = p0.delete(TBL_NOTE, "id=$id", null )
       p0.close()
       return success


   }
}