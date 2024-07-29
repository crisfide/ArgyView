package com.markets.argyview

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.markets.argyview.databinding.ActivityMainBinding
import com.markets.argyview.funciones.CheckMercado
import com.markets.argyview.funciones.CrearActivo
import com.markets.argyview.funciones.Red
import com.markets.argyview.funciones.SnackbarX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var navigation : BottomNavigationView

    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private val mOnNavMenu = BottomNavigationView.OnNavigationItemSelectedListener{item->
        when(item.itemId){
            R.id.itemFavFragment->{
                supportFragmentManager.commit {
                    replace<Frag1Fav>(R.id.fragmentContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemGrafFragment->{
                supportFragmentManager.commit {
                    replace<Frag2Graf>(R.id.fragmentContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemCotizFragment->{
                supportFragmentManager.commit {
                    replace<Frag3Cotiz>(R.id.fragmentContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.itemConfigFragment->{
                supportFragmentManager.commit {
                    replace<Frag4Config>(R.id.fragmentContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("db", 0)
        editor = preferences.edit()

        this.establecerTema()

        navigation = binding.bNav
        navigation.setOnNavigationItemSelectedListener(mOnNavMenu)

        if (savedInstanceState == null){

            this.datosAlCierre()

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<Frag1Fav>(R.id.fragmentContainer)
            }
        }


    }



    private fun establecerTema() {
        val tema = preferences.getInt("tema", 2)
        val mode = when (tema) {
            0 -> AppCompatDelegate.MODE_NIGHT_NO
            1 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun datosAlCierre() {
        if ( ! CheckMercado.cerrado()) {
            editor.putBoolean("datosGuardados", false)
            editor.apply()
            return
        }

        CrearActivo.initPrefs(this)

        val guardados = preferences.getBoolean("datosGuardados", false)
        if (guardados)
            return

        val arrPaneles = resources.getStringArray(R.array.paneles)
        lifecycleScope.launch {
            try {
                if (!Red.isConnected(this@MainActivity)){
                    throw Exception("No hay conexión a internet")
                }

                arrPaneles.forEach {
                    val json = Red.conectar(CrearActivo.Urls.urlsByma[it]!!, CrearActivo.bodyByma)
                    editor.putString("json-$it", json)
                    editor.apply()
                }

            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    SnackbarX.err(binding.root, "${e.message}")
                }
            }
        }

    }

}