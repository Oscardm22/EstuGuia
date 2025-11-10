package com.oscardm22.estuguia.presentation.features.auth.ui.components.input

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import androidx.core.view.isVisible

/**
 * Maneja todos los TextWatchers y la limpieza automática de errores en campos de entrada
 */
class InputFieldManager {

    /**
     * Configura un TextWatcher que limpia errores automáticamente al escribir
     */
    fun setupErrorClearingWatcher(
        editText: EditText,
        inputLayout: TextInputLayout,
        errorTextView: TextView? = null
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Limpiar error del campo individual
                if (inputLayout.error != null) {
                    inputLayout.error = null
                }
                // También limpiar error general si existe
                errorTextView?.takeIf { it.isVisible }?.let {
                    it.visibility = TextView.GONE
                }
            }
        })
    }

    /**
     * Configura un TextWatcher para campos de contraseña que también limpia el error de confirmación
     */
    fun setupPasswordWatcher(
        passwordEditText: EditText,
        passwordLayout: TextInputLayout,
        confirmPasswordEditText: EditText? = null,
        confirmPasswordLayout: TextInputLayout? = null,
        errorTextView: TextView? = null
    ) {
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Limpiar error de la contraseña
                if (passwordLayout.error != null) {
                    passwordLayout.error = null
                }
                // Limpiar error de confirmación si existe y hay texto
                if (confirmPasswordLayout?.error != null &&
                    confirmPasswordEditText?.text?.isNotEmpty() == true) {
                    confirmPasswordLayout.error = null
                }
                // Limpiar error general
                errorTextView?.takeIf { it.isVisible }?.let {
                    it.visibility = TextView.GONE
                }
            }
        })
    }

    /**
     * Configura múltiples campos a la vez
     */
    fun setupMultipleFields(
        fields: List<FieldConfig>,
        errorTextView: TextView? = null
    ) {
        fields.forEach { config ->
            when {
                config.isPasswordField -> setupPasswordWatcher(
                    passwordEditText = config.editText,
                    passwordLayout = config.inputLayout,
                    confirmPasswordEditText = config.relatedEditText,
                    confirmPasswordLayout = config.relatedInputLayout,
                    errorTextView = errorTextView
                )
                else -> setupErrorClearingWatcher(
                    editText = config.editText,
                    inputLayout = config.inputLayout,
                    errorTextView = errorTextView
                )
            }
        }
    }

    /**
     * Configuración para un campo individual
     */
    data class FieldConfig(
        val editText: EditText,
        val inputLayout: TextInputLayout,
        val isPasswordField: Boolean = false,
        val relatedEditText: EditText? = null,
        val relatedInputLayout: TextInputLayout? = null
    )
}