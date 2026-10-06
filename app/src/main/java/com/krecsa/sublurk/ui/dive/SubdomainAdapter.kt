package com.krecsa.sublurk.ui.dive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krecsa.sublurk.databinding.ItemSubdomainBinding

class SubdomainAdapter(
    private val onClick: (String) -> Unit = {},
) : ListAdapter<String, SubdomainAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSubdomainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClick)
    }

    class ViewHolder(
        private val binding: ItemSubdomainBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subdomain: String, onClick: (String) -> Unit) {
            binding.subdomainText.text = subdomain
            binding.root.setOnClickListener { onClick(subdomain) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}