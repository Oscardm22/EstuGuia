package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.isVisible

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Forzar color de íconos blancos en la barra de estado
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupObservers()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        loginButton = findViewById(R.id.loginButton)
        errorTextView = findViewById(R.id.errorTextView)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        registerText = findViewById(R.id.registerText)
    }

    private fun setupObservers() {
        viewModel.loginStateLiveData.observe(this) { state ->
            if (state.isLoading) {
                loginButton.text = getString(R.string.loading)
                loginButton.isEnabled = false
            } else {
                loginButton.text = getString(R.string.login_button)
                loginButton.isEnabled = true
            }

            if (state.isError) {
                showError(state.errorMessage ?: getString(R.string.error_unknown))
            } else {
                hideErrors()
            }

            if (state.isSuccess) {
                navigateToMain()
            }
        }

        viewModel.isLoadingLiveData.observe(this) { isLoading ->
            loginButton.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            if (validateForm(email, password)) {
                viewModel.login(email, password)
            }
        }

        forgotPasswordText.setOnClickListener {
            navigateToForgotPassword()
        }

        registerText.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun setupTextWatchers() {
        // Listener para el campo de email
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Limpiar error cuando el usuario comienza a escribir
                if (emailInputLayout.error != null) {
                    emailInputLayout.error = null
                }
                // También limpiar el error general si existe
                if (errorTextView.isVisible) {
                    errorTextView.visibility = View.GONE
                }
            }
        })

        // Listener para el campo de contraseña
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (passwordInputLayout.error != null) {
                    passwordInputLayout.error = null
                }
                // También limpiar el error general si existe
                if (errorTextView.isVisible) {
                    errorTextView.visibility = View.GONE
                }
            }
        })
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true

        // Validar email
        if (email.isEmpty()) {
            emailInputLayout.error = getString(R.string.error_complete_fields)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            emailInputLayout.error = null
        }

        // Validar contraseña
        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.error_complete_fields)
            isValid = false
        } else if (password.length < 6) {
            passwordInputLayout.error = getString(R.string.error_short_password)
            isValid = false
        } else {
            passwordInputLayout.error = null
        }

        return isValid
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE

        // Ocultar error después de 5 segundos
        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 5000)
    }

    private fun hideErrors() {
        errorTextView.visibility = View.GONE
        emailInputLayout.error = null
        passwordInputLayout.error = null
    }

    private fun navigateToMain() {
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a MainActivity
        // startActivity(Intent(this, MainActivity::class.java))
        // finish()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToForgotPassword() {
        Toast.makeText(this, getString(R.string.navigate_forgot_password), Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a ForgotPasswordActivity
        // startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        errorTextView.handler?.removeCallbacksAndMessages(null)
    }
}