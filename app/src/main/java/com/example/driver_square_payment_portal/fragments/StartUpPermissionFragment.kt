package com.example.driver_square_payment_portal.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.driver_square_payment_portal.Helpers.PermissionHelper

import com.example.driver_square_payment_portal.R
import java.util.*

class StartUpPermissionFragment : Fragment() {

    companion object {
        fun newInstance() = StartUpPermissionFragment()
    }

    private lateinit var viewModel: StartUpPermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.start_up_permission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartUpPermissionViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionHelper.getPermissions(requireActivity())
    }




}
