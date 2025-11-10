package com.oscardm22.estuguia.domain.models

/**
 * Modelo de dominio - independiente de Firebase
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val grade: String,
    val section: String? = null,
    val school: String? = null,
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false
) {
    /**
     * Función de validación básica para registro
     */
    fun isValidForRegistration(): Boolean {
        return email.isNotBlank() &&
                name.isNotBlank() &&
                grade.isNotBlank()
    }

    /**
     * Nombre completo formateado para mostrar en UI
     */
    fun getDisplayName(): String {
        return name.trim().split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    /**
     * Información académica formateada para secundaria
     */
    fun getAcademicInfo(): String {
        return if (section != null) {
            "$grade° de secundaria - Sección $section"
        } else {
            "$grade° de secundaria"
        }
    }

    /**
     * Grado numérico para ordenamiento y cálculos
     */
    fun getGradeNumber(): Int {
        return when (grade.lowercase()) {
            "1ero", "1ro", "primero" -> 1
            "2do", "segundo" -> 2
            "3ero", "3ro", "tercero" -> 3
            "4to", "cuarto" -> 4
            "5to", "quinto" -> 5
            else -> 0
        }
    }
}

/**
 * Grados de secundaria predefinidos
 */
enum class Grade {
    PRIMERO,
    SEGUNDO,
    TERCERO,
    CUARTO,
    QUINTO
}

/**
 * Secciones comunes
 */
enum class Section {
    A, B, C, D, E
}