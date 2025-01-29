package com.markets.argyview

import android.annotation.SuppressLint
import android.content.SharedPreferences
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.FragmentFrag3CotizBinding
import com.markets.argyview.funciones.CheckMercado
import com.markets.argyview.funciones.CrearActivo
import com.markets.argyview.funciones.CrearActivo.Companion
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
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var tickersFav : HashSet<String>

    private val cerrado = CheckMercado.cerrado()

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

        if (!cerrado && !Red.isConnected(this.requireActivity() as AppCompatActivity)){
            SnackbarX.noInternet(binding.root)
        }

        preferences = this.requireActivity().getSharedPreferences("db", 0)
        editor = preferences.edit()
        tickersFav = HashSet(preferences.getStringSet("tickers", hashSetOf())!!)

        val panel = preferences.getString("panelCotiz", "Acciones")
        binding.spinnerCotiz.setSelection(arrPaneles.indexOf(panel))

        binding.spinnerCotiz.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                viewLifecycleOwner.lifecycleScope.launch{
                    try {
                        if (!cerrado) {
                            if (!Red.isConnected(requireActivity() as AppCompatActivity)) {
                                throw Exception("No hay conexión a internet")
                            }
                            SnackbarX.cargando(binding.root)

                        }
                        editor.putString("panelCotiz",arrPaneles[position])
                        editor.apply()
                        cargarCotiz(arrPaneles[position])
                    }catch (e:Exception){
                        SnackbarX.err(binding.root, "${e.message}")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.swipePLayout.setColorSchemeResources(R.color.sube, R.color.baja)
        binding.swipePLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch{
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    try {
                        if (!cerrado) {
                            if (!Red.isConnected(this@Frag3Cotiz.requireActivity() as AppCompatActivity)) {
                                throw Exception("No hay conexión a internet")
                            }
                        }
                        cargarCotiz(arrPaneles[binding.spinnerCotiz.selectedItemPosition])
                    }catch (e:Exception){
                        Log.e("swipeLayout", e.message.toString())
                        SnackbarX.err(binding.root, "${e.message}")

                    }
                    binding.swipePLayout.isRefreshing = false
                }
            }
        }



    }

    @SuppressLint("NotifyDataSetChanged")
    suspend fun cargarCotiz(tipo:String){
        try {

            if (binding.rvCotiz.adapter==null){

                withContext(Dispatchers.IO){

                    listado.addAll(CrearActivo.crearPanelBYMA(tipo).toMutableList())
                    listado.sortBy { it.ticker }

                }
                //Log.i("spinner-creando",listado.joinToString("-") { it.ticker })
                binding.rvCotiz.adapter = Activo3Adapter(listado,this)
                val manager = LinearLayoutManager(this.requireContext())
                binding.rvCotiz.layoutManager = manager
                binding.rvCotiz.addItemDecoration(DividerItemDecoration(this.requireContext(),manager.orientation))
            }else{

                listado.removeAll(listado)
                binding.rvCotiz.adapter!!.notifyDataSetChanged()

                withContext(Dispatchers.IO){

                    val json = CrearActivo.obtenerJson(tipo)
                    listado.addAll(CrearActivo.crearPanelBYMA(tipo, json))
                    listado.sortBy { it.ticker }

                    val jsonEnSharedPref = preferences.getString("json-$tipo", null)
                    if (jsonEnSharedPref == null) {
                        editor.putString("json-$tipo", json)
                        editor.apply()
                    }
                }

                //Log.i("spinner",listado.joinToString("-"){it.ticker})
                //Log.i("spinner","itemcount"+ binding.rvCotiz.adapter!!.itemCount )
                binding.rvCotiz.adapter!!.notifyDataSetChanged()
                //binding.rvCotiz.adapter!!.notifyItemRangeChanged(0, listado.size)
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

