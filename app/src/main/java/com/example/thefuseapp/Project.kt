package com.example.thefuseapp

import android.widget.EditText
import java.sql.Time

data class Project(
    var ProjectName:String? =null, var ProjectDescription:String?=null
    , var ProjectDate: String?=null, var ProjectStartTime: String?=null, var ProjectEndTime: String?=null
    , var ProjectLocation: String?=null, var ProjectCost:String?=null)
