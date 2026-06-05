package com.marketdata.sfmcdemo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.marketdata.sfmcdemo.R
import com.marketdata.sfmcdemo.databinding.FragmentAnalyticsBinding
import com.marketdata.sfmcdemo.model.EventEntry
import com.marketdata.sfmcdemo.viewmodel.AnalyticsViewModel

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.onSuccess { name ->
                val msg = if (name.startsWith("pageview:"))
                    getString(R.string.analytics_pageview_tracked)
                else
                    getString(R.string.analytics_event_tracked, name)
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.md_success, null))
                    .show()
            }.onFailure { e ->
                Snackbar.make(binding.root, e.message ?: "Erro", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.md_error, null))
                    .show()
            }
        }

        viewModel.eventLog.observe(viewLifecycleOwner) { entries ->
            if (entries.isEmpty()) {
                binding.tvEventLog.text = getString(R.string.analytics_log_empty)
            } else {
                binding.tvEventLog.text = entries.joinToString("\n") { formatEntry(it) }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnTrackEvent.setOnClickListener {
            viewModel.trackEvent(
                eventName = binding.etEventName.text?.toString() ?: "",
                attrsJson = binding.etEventAttrs.text?.toString() ?: ""
            )
        }

        binding.btnTrackPageView.setOnClickListener {
            viewModel.trackPageView(
                url = binding.etPageUrl.text?.toString() ?: "",
                title = binding.etPageTitle.text?.toString() ?: ""
            )
        }

        binding.btnClearLog.setOnClickListener {
            viewModel.clearLog()
        }
    }

    private fun formatEntry(entry: EventEntry): String {
        val icon = if (entry.type == "pageview") "🌐" else "📊"
        return "[${entry.formattedTime()}] $icon ${entry.name}" +
                if (entry.detail.isNotBlank()) "\n   ↳ ${entry.detail}" else ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
