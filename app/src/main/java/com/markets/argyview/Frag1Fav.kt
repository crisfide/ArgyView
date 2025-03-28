package com.markets.argyview

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.markets.argyview.activos.Activo
import com.markets.argyview.activos.Bono
import com.markets.argyview.databinding.FragmentFrag1FavBinding
import com.markets.argyview.funciones.BDActivos
import com.markets.argyview.funciones.CheckMercado
import com.markets.argyview.funciones.CrearActivo
import com.markets.argyview.funciones.Red
import com.markets.argyview.funciones.SnackbarX
import com.markets.argyview.recyclerView.ActivoAdapter
import kotlinx.coroutines.launch
import org.json.JSONArray


class Frag1Fav : Fragment() {

    private var _binding: FragmentFrag1FavBinding? = null
    private val binding get() = _binding!!

    private var favoritos = mutableListOf<Activo>()

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val cerrado = CheckMercado.cerrado()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrag1FavBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrPaneles = resources.getStringArray(R.array.paneles)
        BDActivos.inicializarListados(arrPaneles.filter { it!="Opciones" })

        val adapterEdtBuscar = ArrayAdapter(this.requireContext(),R.layout.edt_buscar_item, BDActivos.arr.toList())
        binding.edtBuscar.setAdapter(adapterEdtBuscar)

        if (!cerrado && !Red.isConnected(this.requireActivity() as AppCompatActivity)){
            SnackbarX.noInternet(binding.root)
        }


        preferences = this.requireActivity().getSharedPreferences("db", 0)
        editor = preferences.edit()
        val tickers = preferences.getStringSet("tickers", mutableSetOf())!!.toList()
        viewLifecycleOwner.lifecycleScope.launch{
            try {

                if (savedInstanceState==null){
                    if (!cerrado)
                        SnackbarX.cargando(binding.root)

                    favoritos.addAll(CrearActivo.crear(tickers))
                }else{
                    val jsonStr = savedInstanceState.getString("favoritos")
                    if (jsonStr != null) {
//                        val jsonArray = JSONArray(jsonStr)
//
//                        for (i in 0 until jsonArray.length()) {
//                            val jsonObject = jsonArray.getJSONObject(i)
//                            val tipo = BDActivos.obtenerTipo(jsonObject.getString("ticker"))
//
//                            val papel = if (tipo == "Bonos") {
//                                Gson().fromJson(jsonObject.toString(), Bono::class.java)
//                            } else {
//                                Gson().fromJson(jsonObject.toString(), Activo::class.java)
//                            }
//                            favoritos.add(papel)
//
//                        }


                        val type = object : TypeToken<List<Activo>>() {}.type
                        val lista = Gson().fromJson<List<Activo>>(jsonStr, type)
                        favoritos.addAll(lista)
                    }
                }
                favoritos.sortBy { it.ticker }
                binding.rvFav.adapter = ActivoAdapter(favoritos, this@Frag1Fav)
                val manager = LinearLayoutManager(this@Frag1Fav.requireContext())
                binding.rvFav.layoutManager = manager
                binding.rvFav.addItemDecoration(DividerItemDecoration(this@Frag1Fav.requireContext(),manager.orientation))
            }catch (e:Exception){
                Log.e("prefs", e.message.toString())
            }

        }




        binding.swipePLayout.setColorSchemeResources(R.color.sube, R.color.baja)
        binding.swipePLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    try {
                        if (!cerrado){
                            if (!Red.isConnected(this@Frag1Fav.requireActivity() as AppCompatActivity)){
                                SnackbarX.noInternet(binding.root)
                                throw Exception("No hay conexión a internet")

                            }
                            val tickers2 = favoritos.map { it.ticker }
                            favoritos.clear()
                            favoritos.addAll(CrearActivo.crear(tickers2))
                            binding.rvFav.adapter!!.notifyItemRangeChanged(0,favoritos.size+1)
                        }


                    }catch (e:Exception){
                        Log.e("swipeLayout", e.message.toString())
                    }
                    binding.swipePLayout.isRefreshing=false
                }


            }




        }

        binding.edtBuscar.addTextChangedListener {
            val str = binding.edtBuscar.text.toString().uppercase()
            val cursor = binding.edtBuscar.selectionStart
            if (str == binding.edtBuscar.text.toString()){
                return@addTextChangedListener
            }
            binding.edtBuscar.setText(str)
            binding.edtBuscar.setSelection(cursor)
        }
        binding.edtBuscar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                agregarActivo(binding.edtBuscar.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        binding.edtBuscar.setOnItemClickListener { parent, view, position, id ->
            agregarActivo(binding.edtBuscar.text.toString())
        }


    }

    private fun agregarActivo(ticker: String) {
        try {
            //bajar teclado
            val imm = this.requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtBuscar.getWindowToken(), 0)

            if (!cerrado && !Red.isConnected(this.activity as AppCompatActivity)){
                SnackbarX.noInternet(binding.root)
                return
            }

            favoritos.forEach {
                if (it.ticker == ticker || it.ticker == "$ticker AL30")
                    throw Exception("El activo ya esta en favoritos")
            }
            lifecycleScope.launch {
                try{
                    val activo = CrearActivo.crear(ticker)
                    favoritos.add(activo)
                    binding.rvFav.adapter!!.notifyItemInserted(favoritos.indexOf(activo))
                    guardarPreferences(activo.ticker)
                }catch (e:Exception){
                    SnackbarX.err(binding.root, "${e.message}")
                }
            }
        }catch (e:Exception){
            SnackbarX.err(binding.root, "${e.message}")
        }
    }

    private fun guardarPreferences(ticker: String) {
        val tickers = HashSet(preferences.getStringSet("tickers", hashSetOf())!!)
        tickers.add(ticker)
        editor.putStringSet("tickers",tickers)
        editor.apply()
        Log.i("prefesG",preferences.getStringSet("tickers",null)!!.joinToString(" "))
    }
    fun borrarPreferences(ticker: String) {
        val tickers = HashSet(preferences.getStringSet("tickers", hashSetOf())!!)
        tickers.remove(ticker)
        editor.putStringSet("tickers",tickers)
        editor.apply()
        Log.i("prefesB",preferences.getStringSet("tickers",null)!!.joinToString(" "))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val jsonStr = Gson().toJson(favoritos)
        outState.putString("favoritos", jsonStr)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}