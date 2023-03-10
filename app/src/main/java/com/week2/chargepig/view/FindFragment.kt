package com.week2.chargepig.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.week2.chargepig.R
import com.week2.chargepig.databinding.FragmentFindBinding

class FindFragment : Fragment() {

    private lateinit var binding : FragmentFindBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener {
            navController.navigate(R.id.action_findFragment_to_homeFragment)
        }
    }
}