package com.example.driver_square_payment_portal.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.driver_square_payment_portal.R

class EmailFragment : Fragment() {

    companion object {
        fun newInstance() = EmailFragment()
    }

    private lateinit var viewModel: EmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}