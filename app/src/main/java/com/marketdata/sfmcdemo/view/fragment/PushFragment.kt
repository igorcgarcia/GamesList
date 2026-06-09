package com.marketdata.sfmcdemo.view.fragment

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.marketdata.sfmcdemo.R
import com.marketdata.sfmcdemo.databinding.FragmentPushBinding
import com.marketdata.sfmcdemo.viewmodel.PushViewModel

class PushFragment : Fragment() {

    private var _binding: FragmentPushBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PushViewModel by viewModels()

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            updatePermissionUi(granted)
            if (granted) viewModel.setPushEnabled(true)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPushBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        updatePermissionUi(hasNotificationPermission())
    }

    private fun setupObservers() {
        viewModel.pushEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.switchPush.isChecked = enabled
            binding.tvPushStatusDesc.text =
                if (enabled) getString(R.string.push_enabled_success)
                else getString(R.string.push_disabled_success)
        }

        viewModel.pushToken.observe(viewLifecycleOwner) { token ->
            binding.tvFcmToken.text =
                if (token.isNullOrBlank()) getString(R.string.push_token_empty) else token
        }

        viewModel.lastPayload.observe(viewLifecycleOwner) { payload ->
            binding.tvLastMessage.text =
                if (payload.isNullOrBlank()) getString(R.string.push_no_messages) else payload
        }
    }

    private fun setupClickListeners() {
        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !hasNotificationPermission()) {
                binding.switchPush.isChecked = false
                requestPermission()
            } else {
                viewModel.setPushEnabled(isChecked)
            }
        }

        binding.btnRequestPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                requestPermission()
            } else {
                openAppSettings()
            }
        }

        binding.btnCopyToken.setOnClickListener {
            val token = binding.tvFcmToken.text.toString()
            if (token != getString(R.string.push_token_empty)) {
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("FCM Token", token))
                Snackbar.make(binding.root, getString(R.string.push_token_copied), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        })
    }

    private fun updatePermissionUi(granted: Boolean) {
        binding.tvPermissionStatus.apply {
            text = if (granted) getString(R.string.push_permission_granted)
                   else getString(R.string.push_permission_denied)
            setTextColor(
                resources.getColor(if (granted) R.color.md_success else R.color.md_error, null)
            )
        }
        binding.btnRequestPermission.visibility = if (granted) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
