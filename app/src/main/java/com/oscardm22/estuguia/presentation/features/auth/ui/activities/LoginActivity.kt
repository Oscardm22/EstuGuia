package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.os.Bundle
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
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
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
    }



    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
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
                errorTextView.text = state.errorMessage ?: getString(R.string.error_unknown)
                errorTextView.visibility = TextView.VISIBLE
            } else {
                errorTextView.visibility = TextView.GONE
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

            if (email.isBlank() || password.isBlank()) {
                errorTextView.text = getString(R.string.error_complete_fields)
                errorTextView.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        forgotPasswordText.setOnClickListener {
            navigateToForgotPassword()
        }

        registerText.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToMain() {
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a MainActivity
        // startActivity(Intent(this, MainActivity::class.java))
        // finish()
    }

    private fun navigateToRegister() {
        Toast.makeText(this, getString(R.string.navigate_register), Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a RegisterActivity
        // startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToForgotPassword() {
        Toast.makeText(this, getString(R.string.navigate_forgot_password), Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a ForgotPasswordActivity
        // startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
}