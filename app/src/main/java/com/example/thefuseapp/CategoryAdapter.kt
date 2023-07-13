package com.example.thefuseapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.thefuseapp.databinding.CategoryRowBinding
import com.google.firebase.database.FirebaseDatabase

class CategoryAdapter:RecyclerView.Adapter<CategoryAdapter.HolderCategory>, Filterable{
    private val context: Context
    public var categoryArrayList : ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory>
    private var filter : SearchCategory? = null

    private lateinit var binding: CategoryRowBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = CategoryRowBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }
    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
       val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val hoursCategory = model.hoursCategory
        val timestamp = model.timestamp
        val uid = model.uid

        holder.totalview.text = hoursCategory
        holder.categoryView.text = category
        holder.deleteBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context).setMessage("Delete category").setPositiveButton("Delete"){a, N->
                Toast.makeText(context,"Category Deleting..",Toast.LENGTH_SHORT).show()
                deleteCat(model,holder)
            }.setNegativeButton("Cancel"){a,N->
                a.dismiss()
            }.show()
        }
        holder.itemView.setOnClickListener {
            val intent  = Intent(context,ListofTimesheets::class.java)
            intent.putExtra("categoryId",id)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }

    }

    private fun deleteCat(model: ModelCategory, holder: HolderCategory) {
        val  id = model.id

        val reference = FirebaseDatabase.getInstance().getReference("Categories")
        reference.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(context,"Category Deleting..",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener { N->
            Toast.makeText(context,N.message,Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount():Int{
        return categoryArrayList.size
    }

    inner class  HolderCategory(itemView: View):RecyclerView.ViewHolder(itemView){
        var categoryView : TextView = binding.categoryTV
        var totalview : TextView = binding.totalTV
        var deleteBtn : ImageButton = binding.deleteBTN
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = SearchCategory(filterList,this)
        }
        return filter as SearchCategory
    }


}