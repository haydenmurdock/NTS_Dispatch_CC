package com.example.driver_square_payment_portal.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.driver_square_payment_portal.R
import com.example.driver_square_payment_portal.Helpers.SquareHelper

class CheckOAuthFragment : Fragment() {
    val currentFragmentId = R.id.checkOAuthFragment

    companion object {
        fun newInstance() = CheckOAuthFragment()
    }

    private lateinit var viewModel: CheckOAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.check_o_auth_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CheckOAuthViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isSquareAuthorized = SquareHelper.getAuthStatus()
        if(!isSquareAuthorized){
            SquareHelper.reauthorizeSquare("ccsi_U_1496", this.requireActivity())
        }
        viewModel.isSquareAuthorized()?.observe(this.viewLifecycleOwner, Observer { squareIsAuth ->
            if(squareIsAuth){
                toEnterEnterCardScreen()
            }
        })
    }

    //Navigation
    private fun toEnterEnterCardScreen(){
        val navController = Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment_container
        )
        if (navController.currentDestination?.id == currentFragmentId){
            navController.navigate(R.id.action_checkOAuthFragment_to_tipScreenFragment)
        }
    }
}
