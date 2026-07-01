package org.qxteam.madartask.feature.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.qxteam.madartask.feature.display.databinding.FragmentDisplayBinding

class DisplayFragment : Fragment() {

    private val viewModel: DisplayViewModel by viewModel()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            viewModel.deleteUser(user)
        }
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Handle loading
                    binding.pbLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (!state.isLoading) {
                        if (state.users.isEmpty()) {
                            binding.rvUsers.visibility = View.GONE
                            binding.layoutEmptyState.visibility = View.VISIBLE
                        } else {
                            binding.layoutEmptyState.visibility = View.GONE
                            binding.rvUsers.visibility = View.VISIBLE
                            userAdapter.submitList(state.users)
                        }
                    } else {
                        binding.rvUsers.visibility = View.GONE
                        binding.layoutEmptyState.visibility = View.GONE
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
