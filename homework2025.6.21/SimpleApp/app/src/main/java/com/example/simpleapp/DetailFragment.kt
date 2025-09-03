package com.example.simpleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.simpleapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var item: HomeItem
    private var position: Int = -1

    companion object {
        fun newInstance(item: HomeItem, position: Int) = DetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable("item", item)
                putInt("position", position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.getParcelable("item") ?: throw IllegalArgumentException("Missing item")
            position = it.getInt("position", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 显示内容
        when (item.type) {
            0 -> {
                binding.textDetail.text = item.content
                binding.textDetail.visibility = android.view.View.VISIBLE
                binding.imageDetail.visibility = android.view.View.GONE
            }
            1 -> {
                Glide.with(this).load(item.content).into(binding.imageDetail)
                binding.imageDetail.visibility = android.view.View.VISIBLE
                binding.textDetail.visibility = android.view.View.GONE
            }
        }

        // 初始化点赞状态
        updateLikeStatus()

        // 点赞按钮点击事件
        binding.detailLikeButton.setOnClickListener {
            item.isLiked = !item.isLiked
            updateLikeStatus()

            // 更新首页数据
            val homeFragment = parentFragmentManager.fragments.find { it is HomeFragment } as? HomeFragment
            homeFragment?.updateItem(position, item)
        }
    }

    private fun updateLikeStatus() {
        binding.detailLikeStatus.text = if (item.isLiked) "已点赞" else "点赞中"
        binding.detailLikeButton.setImageResource(
            if (item.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}