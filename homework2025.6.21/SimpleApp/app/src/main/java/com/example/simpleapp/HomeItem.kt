package com.example.simpleapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeItem(
    val id: Int,
    val type: Int, // 0:文本, 1:图片
    val content: String,
    var isLiked: Boolean = false
) : Parcelable