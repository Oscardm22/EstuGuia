package com.oscardm22.estuguia.presentation.features.auth.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.presentation.features.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔥 INICIALIZAR VIEWS
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)
        val registerText = findViewById<TextView>(R.id.registerText)

        // 🔥 OBSERVAR VIEWMODEL - USAR LOS LIVEDATA ADAPTERS
        viewModel.loginStateLiveData.observe(this) { state ->
            if (state.isLoading) {
                loginButton.text = "Cargando..."
                loginButton.isEnabled = false
            } else {
                loginButton.text = "Iniciar Sesión"
                loginButton.isEnabled = true
            }

            if (state.isError) {
                errorTextView.text = state.errorMessage
                errorTextView.visibility = TextView.VISIBLE
            } else {
                errorTextView.visibility = TextView.GONE
            }

            if (state.isSuccess) {
                // Navegar al dashboard (implementar después)
                // startActivity(Intent(this, DashboardActivity::class.java))
                // finish()
            }
        }

        viewModel.isLoadingLiveData.observe(this) { isLoading ->
            loginButton.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
        }

        // 🔥 CONFIGURAR BOTÓN LOGIN
        loginButton.setOnClickListener {
            val email = emailEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            if (email.isBlank() || password.isBlank()) {
                errorTextView.text = "Por favor completa todos los campos"
                errorTextView.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        // 🔥 CONFIGURAR OTROS BOTONES (placeholder por ahora)
        forgotPasswordText.setOnClickListener {
            // Navegar a recuperación de contraseña (implementar después)
            // startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        registerText.setOnClickListener {
            // Navegar a registro (implementar después)
            // startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}