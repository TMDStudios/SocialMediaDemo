package com.example.socialmediademo.models

data class Post(
    val id: Int,
    val user: String,
    val title: String,
    val likes: String,
    val text: String,
    val comments: String)