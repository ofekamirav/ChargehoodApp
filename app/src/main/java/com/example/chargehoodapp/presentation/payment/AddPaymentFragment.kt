package com.example.chargehoodapp.presentation.payment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.FragmentAddPaymentMethodBinding

class AddPaymentFragment : Fragment() {

    private var binding: FragmentAddPaymentMethodBinding? = null
    private var viewModel: AddPaymentViewModel?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPaymentMethodBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AddPaymentViewModel::class.java]

        setupCardSelection()
        setupCardNumberFormatting()
        setupExpiryDateFormatting()

        binding?.cardCvv?.filters = arrayOf(InputFilter.LengthFilter(3))
        binding?.cardCvv?.inputType = InputType.TYPE_CLASS_NUMBER

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding?.saveCardButton?.setOnClickListener {
            val cardNumber = binding?.cardNumber?.text.toString().replace(" ", "")
            val expiry = binding?.cardExpiry?.text.toString()

            viewModel?.savePaymentInfo(cardNumber, expiry)
        }

        // Observe success message
        viewModel?.successMessage?.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        // Observe error message
        viewModel?.errorMessage?.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCardSelection() {
        val cardList = listOf(binding?.visaCard, binding?.amexCard, binding?.mastercardCard)

        cardList.forEach { card ->
            card?.setOnClickListener {
                selectCard(card, cardList)
                when (card.id) {
                    binding?.visaCard?.id -> viewModel?.selectCard("Visa")
                    binding?.amexCard?.id -> viewModel?.selectCard("AmericanExpress")
                    binding?.mastercardCard?.id -> viewModel?.selectCard("MasterCard")
                }
            }
        }
    }

    //handle ui of selected card
    private fun selectCard(selectedCard: ImageView, cardList: List<ImageView?>) {
        cardList.forEach { it?.alpha = 0.5f } // Make unselected cards transparent
        selectedCard.alpha = 1.0f // Highlight selected card
    }

    //handle card number formatting
    private fun setupCardNumberFormatting() {
        binding?.cardNumber?.filters = arrayOf(InputFilter.LengthFilter(19)) // 16 digits + 3 spaces
        binding?.cardNumber?.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                isEditing = true
                val digitsOnly = s.toString().replace(" ", "")
                val formatted = StringBuilder()

                for (i in digitsOnly.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(digitsOnly[i])
                }

                binding?.cardNumber?.setText(formatted.toString())
                binding?.cardNumber?.setSelection(formatted.length)

                isEditing = false
            }
        })
    }

    //handle expiry date formatting
    private fun setupExpiryDateFormatting() {
        binding?.cardExpiry?.filters = arrayOf(InputFilter.LengthFilter(5)) // MM/YY
        binding?.cardExpiry?.inputType = InputType.TYPE_CLASS_NUMBER

        binding?.cardExpiry?.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return

                isEditing = true
                var formatted = s.toString().replace("/", "")

                if (formatted.length > 2) {
                    formatted = formatted.substring(0, 2) + "/" + formatted.substring(2)
                }

                binding?.cardExpiry?.setText(formatted)
                binding?.cardExpiry?.setSelection(formatted.length)

                isEditing = false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
