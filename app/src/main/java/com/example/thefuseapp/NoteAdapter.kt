package com.example.thefuseapp

import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.thefuseapp.databinding.CardItemsNoteBinding

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
private var nmList: ArrayList<NoteModel> = ArrayList()
    private var onClickItem:((NoteModel) -> Unit)? = null
    private var onClickDeleteItem:((NoteModel) -> Unit)? = null
    private lateinit var binding: CardItemsNoteBinding

    fun addItems(items: ArrayList<NoteModel>){
        this.nmList = items
        notifyDataSetChanged()
    }

   fun setOnClickitem(callback:(NoteModel)->Unit){
       this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback:(NoteModel)->Unit){
        this.onClickDeleteItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        binding = CardItemsNoteBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
      return nmList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
       val nm = nmList[position]
        holder.bindView(nm)
        holder.itemView.setOnClickListener{ onClickItem?.invoke(nm) }
        holder.btnDelete.setOnClickListener{onClickDeleteItem?.invoke(nm)}
    }


    inner class NoteViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var id : TextView = binding.tvId
        private var title : TextView= binding.tvTitle
        private var content :TextView = binding.tvContent
        var btnDelete : Button = binding.btnDelete

        fun bindView(std:NoteModel){
            id.text = std.id.toString()
            title.text = std.title
            content.text = std.content
        }
    }


}