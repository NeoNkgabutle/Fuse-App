package com.example.thefuseapp

import android.content.ClipDescription
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, dbname, factory,version ) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table IF NOT EXISTS user(id integer primary key autoincrement," + "name varchar(100), username varchar(100), email varchar(100), password varchar(30))")
        p0?.execSQL("create table IF NOT EXISTS Timesheet(id integer primary key autoincrement," + "timesheetName varchar(100), timesheetDescription varchar(100), timesheetDate text, startDate text, endDate text, location varchar(100), cost double)")
    }



    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertUserData(name: String, username: String, email: String, password: String) {
        val db: SQLiteDatabase = writableDatabase
        val values: ContentValues = ContentValues()
        values.put("name", name)
        values.put("username", username)
        values.put("email", email)
        values.put("password", password)

        db.insert("user", null, values)
        db.close()


    }

    fun insertTimesheetData(timesheetName: String, timesheetDescription: String, timesheetDate: String, startDate: String, endDate: String, location: String, cost: Double){
        val db: SQLiteDatabase = writableDatabase
        val values: ContentValues = ContentValues()
        values.put("timesheetName", timesheetName)
        values.put("timesheetDescription", timesheetDescription)
        values.put("timesheetDate", timesheetDate)
        values.put("startDate", startDate)
        values.put("endDate", endDate)
        values.put("location", location)
        values.put("cost", cost)

        db.insert("Timesheet", null, values)
        db.close()

    }

    fun userPresent(username: String, password: String): Boolean {
        val db = writableDatabase
        val query ="select * from user where username = '$username' and password = '$password'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    fun timesheetDisplay(): Boolean {
        val db = writableDatabase
        val query ="select * from Timesheet "
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    companion object{
        internal val dbname ="loginDB"
        internal val factory = null
        internal val version = 1
    }
}