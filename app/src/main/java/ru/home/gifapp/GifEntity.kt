package ru.home.gifapp

import com.google.gson.annotations.SerializedName

data class GifEntity(
    val id: Long,
    val description: String,
    val votes: Int,
    val author: String,
    val gifURL: String,
    val previewURL: String
)

class GifNetworkWrapper(@SerializedName("result") val gifList: List<GifEntity>)
