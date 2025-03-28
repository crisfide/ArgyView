package com.markets.argyview.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.markets.argyview.Frag1Fav
import com.markets.argyview.R
import com.markets.argyview.activos.Activo
import com.markets.argyview.databinding.RvFavItemBinding
import com.markets.argyview.funciones.SnackbarX


class ActivoAdapter(var favoritos:MutableList<Activo>, val frag1Fav: Frag1Fav)
    : RecyclerView.Adapter<ActivoAdapter.ActivoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ActivoViewHolder(layoutInflater.inflate(R.layout.rv_fav_item,parent,false))
    }

    override fun onBindViewHolder(holder: ActivoViewHolder, position: Int) {
        holder.render(favoritos[position])
    }

    override fun getItemCount(): Int = favoritos.size


    inner class ActivoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = RvFavItemBinding.bind(view)

        fun render(activo: Activo){
            binding.rvFavTicker.text = activo.ticker
            binding.rvFavPrecio.text = activo.precioF
            binding.rvFavDif.text = activo.difF
            val res = frag1Fav.resources
            binding.rvFavDif.setTextColor(if (activo.dif<0) res.getColor(R.color.baja) else res.getColor(R.color.sube))

            binding.rvFavEstrella.setOnClickListener {
                activo.fav=false
                notifyItemRemoved(favoritos.indexOf(activo))
                favoritos.remove(activo)
                frag1Fav.borrarPreferences(activo.ticker)
                Log.i("render", favoritos.joinToString (", "){ it.ticker })
                SnackbarX.normal(binding.root, "Eliminado ${activo.ticker}")
            }

            itemView.setOnClickListener {
                val diag = BottomSheetDialog(frag1Fav.requireContext())
                val view = LayoutInflater.from(frag1Fav.requireActivity().applicationContext).inflate(R.layout.bottom_sheet_dialog,null)

                val txvDetalle = view.findViewById<TextView>(R.id.txvDetalle)

                /*if (activo.dif == 0.0) SnackbarX.normal(itemView,"Precio no disponible")
                else */txvDetalle.text = activo.toString()

                diag.setCancelable(true)
                diag.setContentView(view)


                diag.show()

            }

        }

    }
}