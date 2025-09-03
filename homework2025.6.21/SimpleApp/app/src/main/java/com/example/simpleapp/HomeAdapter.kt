package com.example.simpleapp

import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class HomeAdapter :
    BaseQuickAdapter<HomeItem, BaseViewHolder>(R.layout.item_home) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return super.onCreateDefViewHolder(parent, viewType)
    }

    override fun convert(holder: BaseViewHolder, item: HomeItem?) {
        item ?: return

        val context = holder.itemView.context

        when (item.type) {
            0 -> {
                holder.setText(R.id.text_content, item.content)
                    .setVisible(R.id.text_content, true)
                    .setVisible(R.id.image_content, false)
            }
            1 -> {
                Glide.with(context).load(item.content).into(holder.getView(R.id.image_content))
                holder.setVisible(R.id.image_content, true)
                    .setVisible(R.id.text_content, false)
            }
        }

        holder.setText(R.id.like_status, if (item.isLiked) "已点赞" else "点赞中")
            .setImageResource(
                R.id.like_button,
                if (item.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
            )

        // 点赞按钮点击事件
        holder.getView<ImageView>(R.id.like_button).setOnClickListener {
            item.isLiked = !item.isLiked
            notifyItemChanged(holder.layoutPosition)
        }
    }
}
