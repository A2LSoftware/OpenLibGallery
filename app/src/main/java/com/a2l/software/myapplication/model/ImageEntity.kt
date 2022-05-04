package com.a2l.software.myapplication.model

import java.io.Serializable

data class ImageEntity(
    val id: Long,
    val path: String
) : Serializable