package com.example.thefuseapp
import android.widget.Filter

class SearchCategory:Filter {
    private var filterList:ArrayList<ModelCategory>

    private var adapterCategory : CategoryAdapter

    constructor(filterList: ArrayList<ModelCategory>,adapterCategory: CategoryAdapter):super(){
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filterModels : ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size){
                if (filterList[i].category.uppercase().contains(constraint)){
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterList.size
            results.values = filterList
        }else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>
        adapterCategory.notifyDataSetChanged()
    }
}