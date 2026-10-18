package com.krecsa.sublurk.ui.domain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krecsa.sublurk.databinding.ItemDnsRecordBinding

data class DnsRecord(
    val type: String,
    val data: String,
)

class DnsRecordAdapter : ListAdapter<DnsRecord, DnsRecordAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDnsRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemDnsRecordBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: DnsRecord) {
            binding.typeText.text = record.type
            binding.dataText.text = record.data
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DnsRecord>() {
        override fun areItemsTheSame(oldItem: DnsRecord, newItem: DnsRecord): Boolean =
            oldItem.type == newItem.type && oldItem.data == newItem.data

        override fun areContentsTheSame(oldItem: DnsRecord, newItem: DnsRecord): Boolean =
            oldItem == newItem
    }
}