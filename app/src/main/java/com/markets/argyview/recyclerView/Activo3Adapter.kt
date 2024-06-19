package com.markets.argyview.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.markets.argyview.Frag1Fav
import com.markets.argyview.Frag3Cotiz
import com.markets.argyview.R
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.RvCotizItemBinding
import com.markets.argyview.databinding.RvFavItemBinding
import com.markets.argyview.funciones.SnackbarX


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

        fun render(activo: Activo){
            binding.rvFavTicker.text = activo.ticker
            binding.rvFavPrecio.text = activo.precioF
            binding.rvFavDif.text = activo.difF
            val res = frag3Cotiz.resources
            binding.rvFavDif.setTextColor(if (activo.dif<0) res.getColor(R.color.baja) else res.getColor(R.color.sube))

            binding.rvFavEstrella.setOnClickListener {
                //todo
                Log.i("estrella", activo.ticker)
            }

            itemView.setOnClickListener {
                val txvListado = frag3Cotiz.requireActivity().findViewById<TextView>(R.id.txvListado)

                if (activo.dif == 0.0) SnackbarX.make(itemView,"Precio no disponible",res.getColor(R.color.fondo))
                else txvListado.text = activo.toString()
            }

        }

    }
}