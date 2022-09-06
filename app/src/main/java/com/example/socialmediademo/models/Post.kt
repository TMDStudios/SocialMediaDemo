package com.example.socialmediademo.models

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Int,
    val user: String,
    val title: String,
    val likes: String,
    val text: String,
    val comments: String)