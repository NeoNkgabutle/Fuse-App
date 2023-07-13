package com.example.thefuseapp

import java.sql.Timestamp

class DailyworkhoursModel {
    var uid :String =""
    var id : String =""
    var dateWorked : String =""
    var hoursWorked : String =""
    var minDailyGoal :String =""
    var maxDailyGoal :String =""
    var timestamp : Long = 0

    constructor()
    constructor(uid: String, id: String, dateWorked: String, hoursWorked: String,minDailyGoal:String,maxDailyGoal:String, timestamp: Long) {
        this.uid = uid
        this.id = id
        this.dateWorked = dateWorked
        this.hoursWorked = hoursWorked
        this.minDailyGoal = minDailyGoal
        this.maxDailyGoal = maxDailyGoal
        this.timestamp = timestamp
    }
}