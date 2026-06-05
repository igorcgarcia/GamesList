package com.marketdata.sfmcdemo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.marketdata.sfmcdemo.R
import com.marketdata.sfmcdemo.databinding.FragmentIdentityBinding
import com.marketdata.sfmcdemo.model.ContactData
import com.marketdata.sfmcdemo.viewmodel.IdentityViewModel

class IdentityFragment : Fragment() {

    private var _binding: FragmentIdentityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IdentityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdentityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.loadCurrentValues()
    }

    private fun setupObservers() {
        viewModel.currentContact.observe(viewLifecycleOwner) { contact ->
            binding.tvCurrentValues.text = formatContact(contact)
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, getString(R.string.identity_success), Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.md_success, null))
                    .show()
            }.onFailure { e ->
                Snackbar.make(
                    binding.root,
                    "${getString(R.string.identity_error)}: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).setBackgroundTint(resources.getColor(R.color.md_error, null)).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveIdentity.setOnClickListener {
            val contactKey = binding.etContactKey.text?.toString() ?: ""
            if (contactKey.isBlank()) {
                binding.etContactKey.error = getString(R.string.identity_contact_key_required)
                return@setOnClickListener
            }
            viewModel.saveIdentity(
                contactKey = contactKey,
                firstName = binding.etFirstName.text?.toString() ?: "",
                lastName = binding.etLastName.text?.toString() ?: "",
                email = binding.etEmail.text?.toString() ?: "",
                phone = binding.etPhone.text?.toString() ?: ""
            )
        }
    }

    private fun formatContact(contact: ContactData): String = buildString {
        appendLine("contactKey : ${contact.contactKey.ifBlank { "—" }}")
        appendLine("firstName  : ${contact.firstName.ifBlank { "—" }}")
        appendLine("lastName   : ${contact.lastName.ifBlank { "—" }}")
        appendLine("email      : ${contact.email.ifBlank { "—" }}")
        append    ("phone      : ${contact.phone.ifBlank { "—" }}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
