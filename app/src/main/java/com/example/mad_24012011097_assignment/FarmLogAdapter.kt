package com.example.mad_24012011097_assignment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mad_24012011097_assignment.databinding.ItemFarmLogBinding

class FarmLogAdapter(
    private val onLogClick: (FarmLog) -> Unit,
    private val onDeleteClick: (FarmLog) -> Unit
) : ListAdapter<FarmLog, FarmLogAdapter.FarmLogViewHolder>(FarmLogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmLogViewHolder {
        val binding = ItemFarmLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FarmLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FarmLogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FarmLogViewHolder(
        private val binding: ItemFarmLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: FarmLog) {
            binding.tvCropName.text = log.cropName
            binding.tvActivity.text = log.activityType
            binding.tvDate.text = "Date: ${log.date}"
            binding.tvNotes.text = log.notes.ifBlank { "No notes added." }

            val backgroundColor = when (log.activityType) {
                "Sowing" -> Color.parseColor("#E8F5E9")
                "Harvesting" -> Color.parseColor("#FFF8E1")
                "Irrigation" -> Color.parseColor("#E1F5FE")
                else -> Color.parseColor("#FBE9E7")
            }

            val textColor = when (log.activityType) {
                "Sowing" -> Color.parseColor("#1B5E20")
                "Harvesting" -> Color.parseColor("#F57F17")
                "Irrigation" -> Color.parseColor("#0288D1")
                else -> Color.parseColor("#D84315")
            }

            val activityBadge = GradientDrawable().apply {
                setColor(backgroundColor)
                cornerRadius = 24f
            }

            binding.tvActivity.background = activityBadge
            binding.tvActivity.setTextColor(textColor)

            binding.root.setOnClickListener {
                onLogClick(log)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(log)
            }
        }
    }

    class FarmLogDiffCallback : DiffUtil.ItemCallback<FarmLog>() {

        override fun areItemsTheSame(oldItem: FarmLog, newItem: FarmLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FarmLog, newItem: FarmLog): Boolean {
            return oldItem == newItem
        }
    }
}