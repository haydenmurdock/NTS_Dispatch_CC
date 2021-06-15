package com.example.driver_square_payment_portal.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.driver_square_payment_portal.R

class CustomTipFragment : Fragment() {

    companion object {
        fun newInstance() = CustomTipFragment()
    }

    private lateinit var viewModel: CustomTipViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.custom_tip_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CustomTipViewModel::class.java)

    }

}
