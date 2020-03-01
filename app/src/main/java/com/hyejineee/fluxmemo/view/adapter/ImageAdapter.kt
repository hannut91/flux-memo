package com.hyejineee.fluxmemo.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyejineee.fluxmemo.databinding.ImageItemBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var clickListener: (String) -> Unit = {
    }
    var longClickListener: (Int, String) -> Unit = { _, _ -> }

    var images = listOf<String>()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    inner class ViewHolder(
        private val binding: ImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(path: String, position: Int) {
            binding.apply {
                imagePath = path
                binding.root.setOnClickListener { clickListener(path) }
                binding.root.setOnLongClickListener {
                    longClickListener(position, path)
                    true
                }
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ImageItemBinding.inflate(LayoutInflater.from(parent.context)))

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    fun appendImage(imagePath: String) {
        images = images.plus(imagePath)
    }

    fun deleteImage(position: Int) {
        images = images.toMutableList().apply { removeAt(position) }
    }
}
