/*
 * Copyright 2022-2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.fhir.examples.configurablecare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.fhir.examples.configurablecare.care.CareConfiguration
import com.google.fhir.examples.configurablecare.care.CareWorkflowExecutionViewModel
import com.google.fhir.examples.configurablecare.care.ConfigurationManager
import com.google.fhir.examples.configurablecare.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
  private lateinit var careConfiguration: CareConfiguration
  private val workflowExecutionViewModel by activityViewModels<CareWorkflowExecutionViewModel>()
  private var _binding: FragmentHomeBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    careConfiguration = ConfigurationManager.getCareConfiguration(requireContext())
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = resources.getString(R.string.app_name)
      setDisplayHomeAsUpEnabled(true)
    }
    setHasOptionsMenu(true)
    (activity as MainActivity).setDrawerEnabled(true)
    setIGSpinner()
    setOnClicks()
  }

  private fun setIGSpinner() {
    val igSpinner = binding.igSpinner
    val adapter =
      ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
    careConfiguration.supportedImplementationGuides.forEach {
      adapter.add(it.implementationGuideConfig.entryPoint)
    }
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    igSpinner.adapter = adapter

    igSpinner.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          if (p0 != null) {
            workflowExecutionViewModel.currentIg = p0.getItemAtPosition(p2) as String
            workflowExecutionViewModel.setActiveRequestConfiguration(
              workflowExecutionViewModel.currentIg
            )
          }
        }
        override fun onNothingSelected(p0: AdapterView<*>?) {}
      }
  }

  private fun setOnClicks() {
    binding.itemNewPatient.setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPatientFragment())
    }
    binding.itemPatientList.setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
    }
    binding.itemSearch.setOnClickListener {
      findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPatientList())
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        (requireActivity() as MainActivity).openNavigationDrawer()
        true
      }
      else -> false
    }
  }
}
