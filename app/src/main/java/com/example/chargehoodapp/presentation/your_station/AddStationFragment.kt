package com.example.chargehoodapp.presentation.your_station
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chargehoodapp.databinding.AddStationFragmentBinding

class AddStationFragment : Fragment() {

    private var _binding: AddStationFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddStationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddStationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.validationError.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.ConnectionTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedButton = binding.root.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(checkedId)
            viewModel.connectionType.value = selectedButton?.text?.toString()
        }

        binding.chargingSpeedGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedButton = binding.root.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(checkedId)
            viewModel.chargingSpeed.value = selectedButton?.text?.toString()
        }

        binding.PriceEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.pricePerMinute.value = binding.PriceEditText.text.toString()
            }
        }

        binding.AddStationButton.setOnClickListener {
            if (viewModel.validateInputs()) {
                Toast.makeText(requireContext(), "Station added successfully!", Toast.LENGTH_SHORT).show()
                // Add submission logic here
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
