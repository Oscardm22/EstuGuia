package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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
import com.oscardm22.estuguia.presentation.features.auth.ui.components.form.AuthFormValidator
import com.oscardm22.estuguia.presentation.features.auth.ui.components.error.AuthErrorHandler
import com.oscardm22.estuguia.presentation.features.auth.ui.components.input.InputFieldManager
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.textfield.MaterialAutoCompleteTextView

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private val formValidator = AuthFormValidator()
    private lateinit var errorHandler: AuthErrorHandler
    private val inputManager = InputFieldManager()

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var gradeSpinner: MaterialAutoCompleteTextView
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var gradeInputLayout: TextInputLayout
    private lateinit var registerButton: MaterialButton
    private lateinit var errorTextView: TextView
    private lateinit var loginText: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupGradeSpinner()
        setupObservers()
        setupClickListeners()
        setupTextWatchers()

        errorHandler = AuthErrorHandler(this)
    }

    private fun initializeViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        gradeSpinner = findViewById(R.id.gradeSpinner)
        nameInputLayout = findViewById(R.id.nameInputLayout)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout)
        gradeInputLayout = findViewById(R.id.gradeInputLayout)
        registerButton = findViewById(R.id.registerButton)
        errorTextView = findViewById(R.id.errorTextView)
        loginText = findViewById(R.id.loginText)
    }

    private fun setupGradeSpinner() {
        val grades = arrayOf(
            "1ero - Primero",
            "2do - Segundo",
            "3ero - Tercero",
            "4to - Cuarto",
            "5to - Quinto"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        gradeSpinner.setAdapter(adapter)

        gradeSpinner.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                gradeSpinner.showDropDown()
            }
        }

        gradeSpinner.setOnClickListener {
            if (!gradeSpinner.isPopupShowing) {
                gradeSpinner.showDropDown()
            }
        }

        gradeInputLayout.setEndIconOnClickListener {
            if (!gradeSpinner.isPopupShowing) {
                gradeSpinner.showDropDown()
            } else {
                gradeSpinner.dismissDropDown()
            }
        }

        // Limpiar error cuando se selecciona un item
        gradeSpinner.setOnItemClickListener { _, _, position, _ ->
            gradeInputLayout.error = null
            gradeSpinner.clearFocus()
        }
    }

    private fun setupObservers() {
        viewModel.registerStateLiveData.observe(this) { state ->
            updateUI(state)
        }

        viewModel.isLoadingLiveData.observe(this) { isLoading ->
            registerButton.isEnabled = !isLoading
            registerButton.text = if (isLoading) getString(R.string.loading) else getString(R.string.register_button)
        }
    }

    private fun updateUI(state: com.oscardm22.estuguia.presentation.features.auth.viewmodel.RegisterState) {
        registerButton.isEnabled = !state.isLoading
        registerButton.text = if (state.isLoading) getString(R.string.loading) else getString(R.string.register_button)

        if (state.isError) {
            showError(state.errorMessage)
        } else {
            hideErrors()
        }

        if (state.isSuccess) {
            navigateToMain()
        }
    }

    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            val name = nameEditText.text?.toString()?.trim() ?: ""
            val email = emailEditText.text?.toString()?.trim() ?: ""
            val password = passwordEditText.text?.toString() ?: ""
            val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""
            val grade = extractGradeFromSelection(gradeSpinner.text?.toString()?.trim() ?: "")

            if (formValidator.validateRegisterForm(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    grade = grade,
                    nameLayout = nameInputLayout,
                    emailLayout = emailInputLayout,
                    passwordLayout = passwordInputLayout,
                    confirmPasswordLayout = confirmPasswordInputLayout,
                    gradeLayout = gradeInputLayout
                )
            ) {
                viewModel.register(email, password, name, grade)
            }
        }

        loginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun extractGradeFromSelection(selectedGrade: String): String {
        return when {
            selectedGrade.contains("1ero") -> "1ero"
            selectedGrade.contains("2do") -> "2do"
            selectedGrade.contains("3ero") -> "3ero"
            selectedGrade.contains("4to") -> "4to"
            selectedGrade.contains("5to") -> "5to"
            selectedGrade.contains("Primero") -> "1ero"
            selectedGrade.contains("Segundo") -> "2do"
            selectedGrade.contains("Tercero") -> "3ero"
            selectedGrade.contains("Cuarto") -> "4to"
            selectedGrade.contains("Quinto") -> "5to"
            else -> selectedGrade
        }
    }

    private fun setupTextWatchers() {

        val fields = listOf(
            InputFieldManager.FieldConfig(
                editText = nameEditText,
                inputLayout = nameInputLayout
            ),
            InputFieldManager.FieldConfig(
                editText = emailEditText,
                inputLayout = emailInputLayout
            ),
            InputFieldManager.FieldConfig(
                editText = passwordEditText,
                inputLayout = passwordInputLayout,
                isPasswordField = true,
                relatedEditText = confirmPasswordEditText,
                relatedInputLayout = confirmPasswordInputLayout
            ),
            InputFieldManager.FieldConfig(
                editText = confirmPasswordEditText,
                inputLayout = confirmPasswordInputLayout
            )
        )

        inputManager.setupMultipleFields(
            fields = fields,
            errorTextView = errorTextView
        )

        // Configurar el spinner por separado
        inputManager.setupErrorClearingWatcher(
            editText = gradeSpinner,
            inputLayout = gradeInputLayout,
            errorTextView = errorTextView
        )
    }

    private fun showError(message: String?) {
        val errorMessage = errorHandler.handleRegistrationError(message)

        errorTextView.text = errorMessage
        errorTextView.visibility = View.VISIBLE

        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 7000)
    }

    private fun hideErrors() {
        errorTextView.visibility = View.GONE
        nameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
        gradeInputLayout.error = null
    }

    private fun navigateToMain() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        errorTextView.handler?.removeCallbacksAndMessages(null)
    }
}