package com.markets.argyview.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.markets.argyview.Frag3Cotiz
import com.markets.argyview.R
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.RvCotizItemBinding
import com.markets.argyview.funciones.SnackbarX
import kotlin.math.log


class Activo3Adapter(var listado:List<Activo>, val frag3Cotiz: Frag3Cotiz)
    : RecyclerView.Adapter<Activo3Adapter.ActivoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ActivoViewHolder(layoutInflater.inflate(R.layout.rv_cotiz_item,parent,false))
    }

    override fun onBindViewHolder(holder: ActivoViewHolder, position: Int) {
        holder.render(listado[position])
    }

    override fun getItemCount(): Int = listado.size


    inner class ActivoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = RvCotizItemBinding.bind(view)

        val preferences = frag3Cotiz.preferences
        val editor = frag3Cotiz.editor
        val tickersFav = frag3Cotiz.tickersFav

        fun render(activo: Activo){
            binding.rvFavTicker.text = activo.ticker
            binding.rvFavPrecio.text = activo.precioF
            binding.rvFavDif.text = activo.difF
            val res = frag3Cotiz.resources
            binding.rvFavDif.setTextColor(
                if (activo.dif<0) res.getColor(R.color.baja)
                else res.getColor(R.color.sube))

            if (!tickersFav.contains(activo.ticker)){
                binding.rvFavEstrella.setImageDrawable(res.getDrawable(R.drawable.fav_star_no))
                binding.rvFavEstrella.setOnClickListener {
                    tickersFav.add(activo.ticker)
                    editor.putStringSet("tickers",tickersFav)
                    editor.apply()
                    //Log.i("prefesG",tickersFav.joinToString(" ") + preferences.getStringSet("tickers",null)!!.joinToString(" "))
                    SnackbarX.normal(this.itemView,"${activo.ticker} fué agregado a favoritos")
                    render(activo)
                }
            }else{
                binding.rvFavEstrella.setImageDrawable(res.getDrawable(R.drawable.fav_star))
                binding.rvFavEstrella.setOnClickListener {
                    tickersFav.remove(activo.ticker)
                    editor.putStringSet("tickers",tickersFav)
                    editor.apply()
                    //Log.i("prefesB",tickersFav.joinToString(" ") + preferences.getStringSet("tickers",null)!!.joinToString(" "))
                    SnackbarX.normal(this.itemView,"${activo.ticker} fué eliminado de favoritos")
                    render(activo)
                }
            }

            itemView.setOnClickListener {
                val diag = BottomSheetDialog(frag3Cotiz.requireContext())
                val view = LayoutInflater.from(frag3Cotiz.requireActivity().applicationContext).inflate(R.layout.bottom_sheet_dialog,null)

                val txvDetalle = view.findViewById<TextView>(R.id.txvDetalle)

                if (activo.dif == 0.0) SnackbarX.normal(itemView,"Precio no disponible")
                else txvDetalle.text = activo.toString()

                diag.setCancelable(true)
                diag.setContentView(view)
                diag.show()
            }

        }

    }
}