//package com.example.chargehoodapp.presentation.payment
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//
//class PaymentFragment: Fragment() {
//    private var binding: PaymentFragmentBinding?=null
//    private var viewModel: PaymentViewModel?=null
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = PaymentFragmentBinding.inflate(inflater, container, false)
//        return binding?.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
//
//}
