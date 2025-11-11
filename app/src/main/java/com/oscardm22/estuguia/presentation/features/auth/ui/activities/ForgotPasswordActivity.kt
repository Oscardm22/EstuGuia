package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.core.navigation.AuthNavigation
import com.oscardm22.estuguia.presentation.features.auth.ui.components.form.AuthFormValidator
import com.oscardm22.estuguia.presentation.features.auth.ui.components.error.AuthErrorHandler
import com.oscardm22.estuguia.presentation.features.auth.ui.components.input.InputFieldManager
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private val formValidator = AuthFormValidator()
    private lateinit var errorHandler: AuthErrorHandler
    private val inputManager = InputFieldManager()
    private lateinit var authNavigation: AuthNavigation

    private lateinit var emailEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var sendButton: MaterialButton
    private lateinit var backToLoginButton: MaterialButton
    private lateinit var errorTextView: TextView
    private lateinit var successTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

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

        errorHandler = AuthErrorHandler(this)
        authNavigation = AuthNavigation(this)
    }

    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        sendButton = findViewById(R.id.sendButton)
        backToLoginButton = findViewById(R.id.backToLoginButton)
        errorTextView = findViewById(R.id.errorTextView)
        successTextView = findViewById(R.id.successTextView)
    }

    private fun setupObservers() {
        // Observar el estado de carga general
        viewModel.isLoadingLiveData.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            val email = emailEditText.text?.toString()?.trim() ?: ""

            if (validateForm(email)) {
                sendResetPasswordEmail(email)
            }
        }

        backToLoginButton.setOnClickListener {
            authNavigation.navigateToLogin()
        }
    }

    private fun setupTextWatchers() {
        inputManager.setupErrorClearingWatcher(
            editText = emailEditText,
            inputLayout = emailInputLayout,
            errorTextView = errorTextView
        )
    }

    private fun validateForm(email: String): Boolean {
        val emailValidation = formValidator.validateEmail(email)

        if (!emailValidation.isValid) {
            emailInputLayout.error = emailValidation.errorMessage
            return false
        }

        emailInputLayout.error = null
        return true
    }

    private fun sendResetPasswordEmail(email: String) {
        showLoading(true)
        hideMessages()

        viewModel.sendPasswordResetEmail(email) { result ->
            showLoading(false)

            result.fold(
                onSuccess = {
                    showSuccess(getString(R.string.reset_link_sent))
                },
                onFailure = { error ->
                    showError(errorHandler.handleRegistrationError(error.message))
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        sendButton.isEnabled = !isLoading
        sendButton.text = if (isLoading) getString(R.string.loading) else getString(R.string.send_reset_link)
    }

    private fun showError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        successTextView.visibility = View.GONE

        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 7000)
    }

    private fun showSuccess(message: String) {
        successTextView.text = message
        successTextView.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE

        successTextView.postDelayed({
            successTextView.visibility = View.GONE
        }, 7000)
    }

    private fun hideMessages() {
        errorTextView.visibility = View.GONE
        successTextView.visibility = View.GONE
    }
}