package com.example.driver_square_payment_portal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationController = Navigation.findNavController(this, R.id.nav_host_fragment_container)


    }
}
