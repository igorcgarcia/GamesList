package com.marketdata.sfmcdemo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.marketdata.sfmcdemo.R
import com.marketdata.sfmcdemo.databinding.FragmentAttributesBinding
import com.marketdata.sfmcdemo.view.adapter.AttributeAdapter
import com.marketdata.sfmcdemo.viewmodel.AttributesViewModel

class AttributesFragment : Fragment() {

    private var _binding: FragmentAttributesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttributesViewModel by viewModels()
    private lateinit var adapter: AttributeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttributesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        adapter = AttributeAdapter { key -> viewModel.removeAttribute(key) }
        binding.rvAttributes.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.attributes.observe(viewLifecycleOwner) { attrs ->
            val list = attrs.entries.toList()
            adapter.submitList(list)
            binding.tvAttrEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAttributes.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.tags.observe(viewLifecycleOwner) { tags ->
            binding.chipGroupTags.removeAllViews()
            binding.tvTagsEmpty.visibility = if (tags.isEmpty()) View.VISIBLE else View.GONE
            tags.forEach { tag ->
                val chip = Chip(requireContext()).apply {
                    text = tag
                    isCloseIconVisible = true
                    setChipBackgroundColorResource(R.color.md_navy_100)
                    setTextColor(resources.getColor(R.color.md_navy_800, null))
                    setCloseIconTintResource(R.color.md_navy_600)
                    setOnCloseIconClickListener { viewModel.removeTag(tag) }
                }
                binding.chipGroupTags.addView(chip)
            }
        }

        viewModel.snackMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddAttribute.setOnClickListener {
            val key = binding.etAttrKey.text?.toString() ?: ""
            val value = binding.etAttrValue.text?.toString() ?: ""
            viewModel.addAttribute(key, value)
            if (key.isNotBlank() && value.isNotBlank()) {
                binding.etAttrKey.text?.clear()
                binding.etAttrValue.text?.clear()
            }
        }

        binding.btnAddTag.setOnClickListener {
            val tag = binding.etTagName.text?.toString() ?: ""
            viewModel.addTag(tag)
            if (tag.isNotBlank()) {
                binding.etTagName.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
