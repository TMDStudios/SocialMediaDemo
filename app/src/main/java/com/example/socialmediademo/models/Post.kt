package com.example.socialmediademo.models

data class Post(
    val id: Int = 0,
    val user: String = "",
    val title: String = "",
    val likes: String = "",
    val text: String = "",
    val comments: String = "")