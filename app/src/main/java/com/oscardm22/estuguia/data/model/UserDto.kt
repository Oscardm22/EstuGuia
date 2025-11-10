package com.oscardm22.estuguia.data.model

import com.google.firebase.firestore.PropertyName

/**
 * Modelo de datos para Firebase Firestore
 * Incluye anotaciones para mapeo con Firestore
 */
data class UserDto(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("email")
    val email: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("grade")
    val grade: String = "",

    @PropertyName("section")
    val section: String? = null,

    @PropertyName("school")
    val school: String? = null,

    @PropertyName("profileImage")
    val profileImage: String? = null,

    @PropertyName("createdAt")
    val createdAt: Long = 0L,

    @PropertyName("isEmailVerified")
    val isEmailVerified: Boolean = false
)