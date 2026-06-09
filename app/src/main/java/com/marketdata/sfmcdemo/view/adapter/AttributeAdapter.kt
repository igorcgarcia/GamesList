package com.marketdata.sfmcdemo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marketdata.sfmcdemo.R

class AttributeAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<Map.Entry<String, String>, AttributeAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Map.Entry<String, String>>() {
            override fun areItemsTheSame(
                old: Map.Entry<String, String>,
                new: Map.Entry<String, String>
            ) = old.key == new.key

            override fun areContentsTheSame(
                old: Map.Entry<String, String>,
                new: Map.Entry<String, String>
            ) = old.key == new.key && old.value == new.value
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKey: TextView = view.findViewById(R.id.tvKey)
        val tvValue: TextView = view.findViewById(R.id.tvValue)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attribute, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        holder.tvKey.text = entry.key
        holder.tvValue.text = entry.value
        holder.btnDelete.setOnClickListener { onDelete(entry.key) }
    }
}
