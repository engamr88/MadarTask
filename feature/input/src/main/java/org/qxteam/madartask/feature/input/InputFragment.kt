package org.qxteam.madartask.feature.input

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.qxteam.madartask.feature.input.databinding.FragmentInputBinding

class InputFragment : Fragment() {

    private val viewModel: InputViewModel by viewModel()
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupInputListeners()
        setupObservers()

        binding.btnSave.setOnClickListener {
            viewModel.saveUser()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_input)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_profiles) {
                findNavController().navigate(org.qxteam.madartask.core.navigation.R.id.action_inputFragment_to_displayFragment)
                true
            } else {
                false
            }
        }
    }

    private fun setupInputListeners() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChange(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onAgeChange(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etJobTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onJobTitleChange(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.cgGender.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedGender = when (checkedIds.firstOrNull()) {
                R.id.chipMale -> "Male"
                R.id.chipFemale -> "Female"
                R.id.chipOther -> "Other"
                else -> "Male"
            }
            viewModel.onGenderChange(selectedGender)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // Name Field
                    if (binding.etName.text.toString() != uiState.name) {
                        binding.etName.setText(uiState.name)
                        binding.etName.setSelection(uiState.name.length)
                    }
                    binding.tilName.error = uiState.nameError

                    // Age Field
                    if (binding.etAge.text.toString() != uiState.age) {
                        binding.etAge.setText(uiState.age)
                        binding.etAge.setSelection(uiState.age.length)
                    }
                    binding.tilAge.error = uiState.ageError

                    // Job Title Field
                    if (binding.etJobTitle.text.toString() != uiState.jobTitle) {
                        binding.etJobTitle.setText(uiState.jobTitle)
                        binding.etJobTitle.setSelection(uiState.jobTitle.length)
                    }
                    binding.tilJobTitle.error = uiState.jobTitleError

                    // Gender Chips
                    val expectedChipId = when (uiState.gender) {
                        "Male" -> R.id.chipMale
                        "Female" -> R.id.chipFemale
                        "Other" -> R.id.chipOther
                        else -> R.id.chipMale
                    }
                    if (binding.cgGender.checkedChipId != expectedChipId) {
                        binding.cgGender.check(expectedChipId)
                    }

                    // Progress & Button States
                    if (uiState.isSaving) {
                        binding.btnSave.isEnabled = false
                        binding.btnSave.text = ""
                        binding.pbSave.visibility = View.VISIBLE
                    } else {
                        binding.btnSave.isEnabled = true
                        binding.btnSave.text = "Save Profile"
                        binding.pbSave.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is InputUiEvent.SaveSuccess -> {
                            Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            Snackbar.make(binding.root, "Profile saved successfully!", Snackbar.LENGTH_SHORT).show()
                        }
                        is InputUiEvent.ShowError -> {
                            Snackbar.make(binding.root, "Error: ${event.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
