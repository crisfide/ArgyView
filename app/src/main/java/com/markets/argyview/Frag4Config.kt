package com.markets.argyview

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import com.markets.argyview.databinding.FragmentFrag4ConfigBinding


class Frag4Config : Fragment() {

    private var _binding: FragmentFrag4ConfigBinding? = null
    private val binding get() = _binding!!

    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrag4ConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = this.requireActivity().getSharedPreferences("db", 0)
        editor = preferences.edit()
        val tema = preferences.getInt("tema", 2)

        val arrTema = arrayOf("Claro", "Oscuro", "Predeterminado del sistema")
        val adapterSpinnerTema = ArrayAdapter<String>(this.requireContext(),R.layout.edt_buscar_item,arrTema)
        adapterSpinnerTema.setDropDownViewResource(R.layout.edt_buscar_item)
        binding.spinnerTema.adapter = adapterSpinnerTema
        binding.spinnerTema.setSelection(tema)

        binding.spinnerTema.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mode = when(position){
                    0-> AppCompatDelegate.MODE_NIGHT_NO
                    1->AppCompatDelegate.MODE_NIGHT_YES
                    else->AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(mode)
                editor.putInt("tema",position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}