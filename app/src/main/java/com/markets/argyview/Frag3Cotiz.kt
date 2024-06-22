package com.markets.argyview

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.FragmentFrag3CotizBinding
import com.markets.argyview.funciones.BDActivos
import com.markets.argyview.funciones.CrearActivo
import com.markets.argyview.funciones.Red
import com.markets.argyview.funciones.SnackbarX
import com.markets.argyview.recyclerView.Activo3Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Frag3Cotiz : Fragment() {

    private var _binding: FragmentFrag3CotizBinding? = null
    private val binding get() = _binding!!

    private var listado = mutableListOf<Activo>()
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrag3CotizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrPaneles = resources.getStringArray(R.array.paneles)
        val adapterSpinnerCotiz = ArrayAdapter(this.requireContext(),R.layout.edt_buscar_item,arrPaneles)
        adapterSpinnerCotiz.setDropDownViewResource(R.layout.edt_buscar_item)
        binding.spinnerCotiz.adapter = adapterSpinnerCotiz

        if (!Red.isConnected(this.requireActivity() as AppCompatActivity)){
            SnackbarX.make(binding.root,"No hay conexión a internet", resources.getColor(R.color.error))
        }

        preferences = this.requireActivity().getSharedPreferences("db", 0)
        editor = preferences.edit()
        val panel = preferences.getString("panelCotiz", "Acciones")
        binding.spinnerCotiz.setSelection(arrPaneles.indexOf(panel))

        binding.spinnerCotiz.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                viewLifecycleOwner.lifecycleScope.launch{
                    try {
                        withContext(Dispatchers.Main){
                            if (!Red.isConnected(requireActivity() as AppCompatActivity)){
                                throw Exception("No hay conexión a internet")
                            }
                            SnackbarX.make(binding.root,"Cargando...",resources.getColor(R.color.fondo))
                        }
                        editor.putString("panelCotiz",arrPaneles[position])
                        editor.apply()
                        cargarCotiz(arrPaneles[position])
                        Log.i("spinnerSelect",arrPaneles[position])
                    }catch (e:Exception){
                        Log.e("spinnerError", e.message.toString())
                        withContext(Dispatchers.Main){
                            SnackbarX.make(binding.root,""+e.message, resources.getColor(R.color.error))
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.swipePLayout.setColorSchemeResources(R.color.sube, R.color.baja)
        binding.swipePLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch{
                try {
                    withContext(Dispatchers.Main){
                        if (!Red.isConnected(this@Frag3Cotiz.requireActivity() as AppCompatActivity)){
                            throw Exception("No hay conexión a internet")
                        }
                    }
                    cargarCotiz(arrPaneles[binding.spinnerCotiz.selectedItemPosition])
                }catch (e:Exception){
                    Log.e("swipeLayout", e.message.toString())
                    withContext(Dispatchers.Main){
                        SnackbarX.make(binding.root,""+e.message, resources.getColor(R.color.error))
                    }
                }
                withContext(Dispatchers.Main){
                    binding.swipePLayout.isRefreshing = false
                }
            }
        }



    }

    suspend fun cargarCotiz(tipo:String){
        try {
            //val arr = CrearActivo.crear(BDActivos.mapa[tipo]!!)

            if (binding.rvCotiz.adapter==null){
                listado.addAll(CrearActivo.crearPanelBolsar(tipo).toMutableList())
                listado.sortBy { it.ticker }
                Log.i("spinner-creando",listado.joinToString("-") { it.ticker })
                binding.rvCotiz.adapter = Activo3Adapter(listado,this)
                val manager = LinearLayoutManager(this.requireContext())
                binding.rvCotiz.layoutManager = manager
                binding.rvCotiz.addItemDecoration(DividerItemDecoration(this.requireContext(),manager.orientation))
                Log.i("spinner", "1ra vez")
            }else{
                listado.removeAll(listado)
                listado.addAll(CrearActivo.crearPanelBolsar(tipo))
                listado.sortBy { it.ticker }
                binding.rvCotiz.adapter!!.notifyItemRangeChanged(0,listado.size)

            }
        }catch (e:Exception){
            Log.e("spinnerError", e.message.toString())
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

