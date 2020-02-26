package com.hyejineee.fluxmemo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyejineee.fluxmemo.databinding.MemoItemBinding
import com.hyejineee.fluxmemo.model.MemoWithImages

class MemoAdapter(
    val clickListener: (Long) -> Unit
) : RecyclerView.Adapter<MemoAdapter.ViewHolder>() {

    var memos = listOf<MemoWithImages>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ViewHolder(
        private val binding: MemoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(memoWithImages: MemoWithImages) {
            binding.apply {
                memo = memoWithImages.memo
                if (memoWithImages.images.isNotEmpty()) {
                    imagePath = memoWithImages.images[0].path
                    memoImage.visibility = View.VISIBLE
                } else {
                    imagePath = ""
                    memoImage.visibility = View.GONE
                }

                root.setOnClickListener { clickListener(memoWithImages.memo.id) }
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MemoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = memos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memos[position])
    }
}
