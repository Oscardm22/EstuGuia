package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.presentation.features.auth.ui.components.form.AuthFormValidator
import com.oscardm22.estuguia.presentation.features.auth.ui.components.error.AuthErrorHandler
import com.oscardm22.estuguia.presentation.features.auth.ui.components.input.InputFieldManager
import com.oscardm22.estuguia.core.navigation.AuthNavigation
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.oscardm22.estuguia.presentation.features.main.ui.activities.MainActivity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.oscardm22.estuguia.domain.usecases.auth.LoginUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private val formValidator = AuthFormValidator()
    private lateinit var errorHandler: AuthErrorHandler
    private val inputManager = InputFieldManager()
    private lateinit var authNavigation: AuthNavigation

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var loginUseCase: LoginUseCase

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

        // VERIFICAR SI YA HAY SESIÓN ACTIVA ANTES DE CARGAR LA VISTA
        checkCurrentUser()

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

        errorHandler = AuthErrorHandler(this)
        authNavigation = AuthNavigation(this)
    }

    private fun checkCurrentUser() {
        lifecycleScope.launch {
            try {
                val isUserLoggedIn = loginUseCase.isUserAlreadyLoggedIn()
                if (isUserLoggedIn) {
                    // Usuario ya está logueado, redirigir directamente a MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

            if (formValidator.validateLoginForm(
                    email = email,
                    password = password,
                    emailLayout = emailInputLayout,
                    passwordLayout = passwordInputLayout
                )
            ) {
                viewModel.login(email, password)
            } else {
                // Mostrar error general si la validación falla
                errorTextView.text = getString(R.string.error_complete_fields)
                errorTextView.visibility = View.VISIBLE
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

        // Para campos normales (email)
        inputManager.setupErrorClearingWatcher(
            editText = emailEditText,
            inputLayout = emailInputLayout,
            errorTextView = errorTextView
        )

        // Para campo de contraseña
        inputManager.setupErrorClearingWatcher(
            editText = passwordEditText,
            inputLayout = passwordInputLayout,
            errorTextView = errorTextView
        )
    }

    private fun showError(message: String) {
        val errorMessage = errorHandler.handleLoginError(message)

        errorTextView.text = errorMessage
        errorTextView.visibility = View.VISIBLE

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
        authNavigation.navigateToMain()
    }

    private fun navigateToRegister() {
        authNavigation.navigateToRegister()
    }

    private fun navigateToForgotPassword() {
        authNavigation.navigateToForgotPassword()
    }

    override fun onDestroy() {
        super.onDestroy()
        errorTextView.handler?.removeCallbacksAndMessages(null)
    }
}