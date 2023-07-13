package com.example.thefuseapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.thefuseapp.databinding.TimesheetRowBinding
import com.google.firebase.storage.FirebaseStorage

class AdapterPDF :RecyclerView.Adapter<AdapterPDF.HoldPDF>,Filterable{

    private var context:Context
    public var pdfArrayList: ArrayList<ModelPdf>
    private val filterList : ArrayList<ModelPdf>
    private lateinit var binding: TimesheetRowBinding

     val filter : FilterTimesheets? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldPDF {
        binding = TimesheetRowBinding.inflate(LayoutInflater.from(context),parent ,false)
        return HoldPDF(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }


    override fun onBindViewHolder(holder: HoldPDF, position: Int) {
       val  model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val Name = model.Name
        val Description = model.Description
        val Date = model.Date
        val StartTime = model.StartTime
        val EndTime = model.EndTime
        val Location = model.Location
        val Cost = model.Cost
        val pdfUrl = model.url
        val TimeImage = model.TimeImage
        val timestamp = model.timestamp
        val dateFormat = TheApplication.TimestampFormate(timestamp)

        holder.TimesheetNameTV.text = Name
        holder.TimesheetDescriptionTV.text = Description
        holder.TimesheetDateTV.text = Date
        holder.TimesheetStartDateTV.text = StartTime
        holder.TimesheetEndDateTV.text = EndTime
        holder.TimesheetLocationTV.text = Location
        Glide.with(holder.itemView).load(TimeImage).transition(DrawableTransitionOptions.withCrossFade()).into(holder.TimesheetImageTV)
        holder.TimesheetCostTV.text = Cost

        TheApplication.loadCategory(categoryId,holder.TimesheetCategoryTV)
        TheApplication.loadPage(pdfUrl,Name)
        TheApplication.loadPDFsize(pdfUrl,Name)

    }

    override fun getFilter(): Filter {
        if(filter == null){
            FilterTimesheets(filterList,this)
        }
        return filter as FilterTimesheets
    }
    inner  class HoldPDF(itemView:View):RecyclerView.ViewHolder(itemView){
        val TimesheetNameTV :TextView= binding.TimesheetName
        val TimesheetDescriptionTV = binding.TimesheetDescription
        val TimesheetCategoryTV = binding.TimesheetCategory
        val TimesheetDateTV = binding.TimesheetDate
        val TimesheetStartDateTV = binding.TimesheetStartTime
        val TimesheetImageTV = binding.TimesheetImage
        val TimesheetEndDateTV = binding.TimesheetEndTime
        val TimesheetLocationTV = binding.TimesheetLocation
        val TimesheetCostTV = binding.TimesheetCost
    }
}