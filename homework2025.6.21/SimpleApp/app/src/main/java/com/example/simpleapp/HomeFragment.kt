package com.example.simpleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), HomeAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter
    private val dataList = mutableListOf<HomeItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 设置点击事件
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.data[position]
            val detailFragment = DetailFragment.newInstance(item, position)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        loadData()
    }


    private fun loadData() {
        dataList.clear()

        for (i in 1..20) {
            val type = if (i % 2 == 0) 0 else 1
            val content = when (type) {
                0 -> "这是一个文本内容 #$i"
                1 -> "https://picsum.photos/300/200?random=$i"
                else -> ""
            }
            dataList.add(HomeItem(i, type, content))
        }

        adapter.setList(dataList)
        binding.swipeRefreshLayout.isRefreshing = false
    }



    override fun onItemClick(item: HomeItem, position: Int) {
        val detailFragment = DetailFragment.newInstance(item, position)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    fun updateItem(position: Int, item: HomeItem) {
        if (position in dataList.indices) {
            dataList[position] = item
            adapter.notifyItemChanged(position)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}