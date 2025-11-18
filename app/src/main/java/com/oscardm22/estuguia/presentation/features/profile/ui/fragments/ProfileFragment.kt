package com.oscardm22.estuguia.presentation.features.profile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.core.navigation.AuthNavigation
import com.oscardm22.estuguia.databinding.FragmentProfileBinding
import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.presentation.features.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var authNavigation: AuthNavigation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()

        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.deleteAccountProgress.visibility = if (state.isDeletingAccount) View.VISIBLE else View.GONE

                    state.user?.let { user ->
                        displayUserInfo(user)
                    }

                    if (state.error != null) {
                        showError(state.error)
                    }

                    // Mostrar/ocultar modos
                    if (state.isEditing) {
                        showEditMode()
                    } else {
                        showViewMode()
                    }

                    // Diálogo de contraseña
                    if (state.showPasswordDialog) {
                        showPasswordDialog()
                    }

                    if (state.shouldNavigateToLogin) {
                        navigateToLogin()
                        viewModel.resetNavigation()
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        authNavigation.navigateToLogin(clearTask = true)
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            viewModel.setEditing(true)
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnCancelEdit.setOnClickListener {
            viewModel.setEditing(false)
        }

        binding.btnChangePassword.setOnClickListener {
            viewModel.setShowPasswordDialog(true)
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmation()
        }
    }

    private fun displayUserInfo(user: User) {
        binding.textUserName.text = user.getDisplayName()
        binding.textUserEmail.text = user.email
        binding.textUserGrade.text = user.getAcademicInfo()

        // En modo edición, llenar los campos
        binding.editTextName.setText(user.name)
        binding.editTextGrade.setText(user.grade)
        binding.editTextSection.setText(user.section ?: "")
        binding.editTextSchool.setText(user.school ?: "")
    }

    private fun showViewMode() {
        binding.layoutViewMode.visibility = View.VISIBLE
        binding.layoutEditMode.visibility = View.GONE
        binding.btnEditProfile.visibility = View.VISIBLE
    }

    private fun showEditMode() {
        binding.layoutViewMode.visibility = View.GONE
        binding.layoutEditMode.visibility = View.VISIBLE
        binding.btnEditProfile.visibility = View.GONE
    }

    private fun saveProfileChanges() {
        val name = binding.editTextName.text.toString()
        val grade = binding.editTextGrade.text.toString()
        val section = binding.editTextSection.text.toString()
        val school = binding.editTextSchool.text.toString()

        if (name.isBlank() || grade.isBlank()) {
            showError("Nombre y grado son obligatorios")
            return
        }

        viewModel.state.value.user?.let { currentUser ->
            val updatedUser = currentUser.copy(
                name = name,
                grade = grade,
                section = if (section.isNotBlank()) section else null,
                school = if (school.isNotBlank()) school else null
            )
            viewModel.updateProfile(updatedUser)
        }

        viewModel.setEditing(false)
    }

    private fun showPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.change_password_dialog_title))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.change_password_button)) { _, _ ->
                val currentPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editCurrentPassword).text.toString()
                val newPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editNewPassword).text.toString()
                val confirmPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editConfirmPassword).text.toString()

                if (newPassword == confirmPassword) {
                    viewModel.updatePassword(currentPassword, newPassword)
                } else {
                    showError(getString(R.string.passwords_do_not_match))
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                viewModel.setShowPasswordDialog(false)
            }
            .setOnDismissListener {
                viewModel.setShowPasswordDialog(false)
            }
            .create()

        dialog.show()

        viewModel.setShowPasswordDialog(false)
    }

    private fun showDeleteAccountConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_account))
            .setMessage("¿Estás seguro de eliminar tu cuenta? Esta acción:\n\n• Eliminará tu cuenta permanentemente\n• Borrará todos tus horarios de clase\n• Eliminará todas tus tareas y recordatorios\n\n⚠️ Esta acción NO se puede deshacer")
            .setPositiveButton("Eliminar Todo") { _, _ ->
                viewModel.deleteAccount()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showError(message: String) {
        com.google.android.material.snackbar.Snackbar.make(binding.root, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
        viewModel.clearError()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}