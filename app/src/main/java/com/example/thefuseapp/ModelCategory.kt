package com.example.thefuseapp


class ModelCategory{

    var id:String = ""
    var category:String =""
    var hoursCategory = ""
    var timestamp:Long = 0
    var uid:String =""

    constructor()
    constructor(id:String, category: String,hoursCategory : String, timestamp: Long, uid: String){
        this.id = id
        this.category = category
        this.hoursCategory = hoursCategory
        this.timestamp = timestamp
        this.uid = uid
    }
}