package com.example.driver_square_payment_portal.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.driver_square_payment_portal.R

class TextMessageFragment : Fragment() {

    companion object {
        fun newInstance() = TextMessageFragment()
    }

    private lateinit var viewModel: TextMessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.text_message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TextMessageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}