package com.markets.argyview

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.markets.argyview.databinding.FragmentFrag4ConfigBinding
import com.markets.argyview.funciones.SnackbarX


class Frag4Config : Fragment() {

    private var _binding: FragmentFrag4ConfigBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrag4ConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtConfigFalopa.setOnClickListener {
            SnackbarX.make(binding.root,"holarlem 4", Color.BLACK)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}