package com.example.thefuseapp

import android.util.Log
import android.widget.Filter
import android.widget.Toast
import com.example.thefuseapp.databinding.ActivityAllTimesheetsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FilterTimesheets : Filter{

    var TAG = "FILTER"
    var filterList: ArrayList<ModelPdf>
    private lateinit var binding: ActivityAllTimesheetsBinding
    var adapterPDF : AdapterPDF

    constructor(filterList: ArrayList<ModelPdf>, adapterPDF: AdapterPDF) {
        this.filterList = filterList
        this.adapterPDF = adapterPDF
    }

    fun parseDate(dateString:String): Date {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        return format.parse(dateString)
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var result = FilterResults()
        if(constraint.isNullOrEmpty()){
            result.values = filterList
            result.count = filterList.size
            Log.d(TAG,"No INPUT")
        }else{
            var filteredTimeSheets = ArrayList<ModelPdf>()

            for (i in filterList.indices){
                var startDate = parseDate(binding.StartDateFilter.text.toString())
                var endDate = parseDate(binding.EndDateFilter.text.toString())
                var timesheetDate = parseDate(filterList[i].Date)
                if (timesheetDate in (startDate..endDate)){
                    filteredTimeSheets.add(filterList[i])
                }
            }
            Log.d(TAG,"List is Filterd")
            result.values = filteredTimeSheets
            result.count = filteredTimeSheets.size

        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterPDF.pdfArrayList = results!!.values as ArrayList<ModelPdf>

        adapterPDF.notifyDataSetChanged()
    }

}