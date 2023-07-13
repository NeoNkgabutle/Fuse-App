package com.example.thefuseapp

class ModelPdf {
    var uid:String =""
    var id:String =""
    var Name : String =""
    var Description:String =""
    var categoryId : String =""
    var Date : String =""
    var StartTime : String =""
    var EndTime : String =""
    var Location : String =""
    var Cost : String =""
    var hoursWorked :String =""
    var url :String =""
    var TimeImage = ""
    var timestamp : Long = 0
    var viewsCount : Long = 0
    var downloadsCount : Long = 0

    constructor()
    constructor(
        uid: String,
        id: String,
        Name: String,
        Description: String,
        categoryId: String,
        Date: String,
        StartTime: String,
        EndTime: String,
        Location: String,
        Cost: String,
        hoursWorked :String,
        url: String,
        TimeImage: String,
        timestamp: Long,
        viewsCount: Long,
        downloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.Name = Name
        this.Description = Description
        this.categoryId = categoryId
        this.Date = Date
        this.StartTime = StartTime
        this.EndTime = EndTime
        this.Location = Location
        this.Cost = Cost
        this.hoursWorked = hoursWorked
        this.url = url
        this.TimeImage = TimeImage
        this.timestamp = timestamp
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount
    }

}