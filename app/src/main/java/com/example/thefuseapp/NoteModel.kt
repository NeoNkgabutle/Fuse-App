package com.example.thefuseapp

import java.util.*


data class NoteModel(
    var id : Int = getAutoId(),
    var title: String ="",
    var content: String = ""
){
    companion object{
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }

}
