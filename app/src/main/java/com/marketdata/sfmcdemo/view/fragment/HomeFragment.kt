package com.marketdata.sfmcdemo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.marketdata.sfmcdemo.R
import com.marketdata.sfmcdemo.databinding.FragmentHomeBinding
import com.marketdata.sfmcdemo.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupObservers() {
        viewModel.isDemo.observe(viewLifecycleOwner) { isDemo ->
            binding.demoModeBanner.visibility = if (isDemo) View.VISIBLE else View.GONE
            binding.tvSdkStatus.apply {
                text = if (isDemo)
                    getString(R.string.home_sdk_demo_mode)
                else
                    getString(R.string.home_sdk_active)
                background = context.getDrawable(
                    if (isDemo) R.drawable.bg_status_chip_demo
                    else R.drawable.bg_status_chip
                )
                setTextColor(resources.getColor(
                    if (isDemo) R.color.md_warning else R.color.md_success,
                    null
                ))
            }
        }

        viewModel.contactKey.observe(viewLifecycleOwner) { key ->
            binding.tvContactKey.text =
                if (key.isNullOrBlank()) getString(R.string.home_contact_key_empty) else key
        }

        viewModel.pushEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.tvPushStatus.apply {
                text = if (enabled)
                    getString(R.string.home_push_enabled)
                else
                    getString(R.string.home_push_disabled)
                setTextColor(resources.getColor(
                    if (enabled) R.color.md_success else R.color.md_gray_400,
                    null
                ))
            }
        }

        viewModel.fcmToken.observe(viewLifecycleOwner) { token ->
            binding.tvFcmToken.text =
                if (token.isNullOrBlank()) "Token não disponível" else token
        }
    }

    private fun setupClickListeners() {
        binding.btnGoIdentity.setOnClickListener {
            findNavController().navigate(R.id.identityFragment)
        }
        binding.btnGoPush.setOnClickListener {
            findNavController().navigate(R.id.pushFragment)
        }
        binding.btnGoAnalytics.setOnClickListener {
            findNavController().navigate(R.id.analyticsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
