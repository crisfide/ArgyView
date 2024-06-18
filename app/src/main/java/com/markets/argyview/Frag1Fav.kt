package com.markets.argyview

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.FragmentFrag1FavBinding
import com.markets.argyview.funciones.BDActivos
import com.markets.argyview.funciones.CrearActivo
import com.markets.argyview.funciones.Red
import com.markets.argyview.funciones.SnackbarX
import com.markets.argyview.recyclerView.ActivoAdapter


class Frag1Fav : Fragment() {

    private var _binding: FragmentFrag1FavBinding? = null
    private val binding get() = _binding!!

    private var favoritos = mutableListOf<Activo>()
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrag1FavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = this.requireActivity().getSharedPreferences("db", 0)
        editor = preferences.edit()
        val tickers = preferences.getStringSet("tickers", mutableSetOf())!!.toList()
        favoritos += CrearActivo.crear(tickers)
        favoritos.sortBy { it.ticker }

        val adapterEdtBuscar = ArrayAdapter(this.requireContext(),R.layout.edt_buscar_item, BDActivos.arr)
        binding.edtBuscar.setAdapter(adapterEdtBuscar)

        binding.rvFav.adapter = ActivoAdapter(favoritos, this)
        val manager = LinearLayoutManager(this.requireContext())
        binding.rvFav.layoutManager = manager
        binding.rvFav.addItemDecoration(DividerItemDecoration(this.requireContext(),manager.orientation))

        binding.swipePLayout.setColorSchemeResources(R.color.sube, R.color.baja)
        binding.swipePLayout.setOnRefreshListener {
            try {
                val tickers = favoritos.map { it.ticker }
                tickers.forEach { Log.e("swipeERROR",it) }
                favoritos.removeAll(favoritos)
                favoritos.addAll(CrearActivo.crear(tickers))
                binding.rvFav.adapter!!.notifyItemRangeChanged(0,favoritos.size)
                binding.swipePLayout.isRefreshing=false

            }catch (e:Exception){
                SnackbarX.make(binding.root,e.message.toString(),resources.getColor(R.color.error))
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
                SnackbarX.make(binding.root,binding.edtBuscar.text.toString(),Color.BLACK)
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

            if (!Red.isConnected(this.activity as AppCompatActivity)){
                SnackbarX.make(binding.root,"No hay conexión a internet", resources.getColor(R.color.error))
                return
            }

            favoritos.forEach {
                if (it.ticker == ticker || it.ticker == "$ticker AL30")
                    throw Exception("El activo ya esta en favoritos")
            }
            val activo = CrearActivo.crear(ticker)
            favoritos.add(activo!!)
            binding.rvFav.adapter!!.notifyItemInserted(favoritos.indexOf(activo))

            guardarPreferences(activo.ticker)

        }catch (e:Exception){
            SnackbarX.make(binding.root,"Error " + e.message, resources.getColor(R.color.error))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}