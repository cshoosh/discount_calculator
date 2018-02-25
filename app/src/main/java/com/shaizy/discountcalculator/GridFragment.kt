package com.shaizy.discountcalculator

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Created by syedshahnawazali on 25/02/2018.

 */

class GridFragment : DialogFragment() {

    var onItemClick: ((Int)->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_grid, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view as RecyclerView

        recycler.layoutManager = GridLayoutManager(view.context, 3)
        recycler.adapter = GridAdapter(arguments?.getParcelableArrayList(KEY_LIST)!!)

    }

    companion object {
        private const val KEY_LIST = "keyList"

        fun newInstance(list: ArrayList<Info>): GridFragment {
            val args = Bundle()
            val frag = GridFragment()

            args.putParcelableArrayList(KEY_LIST, list)
            frag.arguments = args
            return frag
        }
    }

    inner class GridAdapter(private val list: ArrayList<Info>) : RecyclerView.Adapter<GridViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GridViewHolder =
                GridViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(R.layout.view_grid_image, parent, false))


        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: GridViewHolder?, position: Int) {
            val item = list[position]
            if (item.uri.isNotBlank()) {
                val uri = Uri.parse(item.uri)

                Picasso.with(holder?.itemView?.context)
                        .load(uri)
                        .error(R.drawable.no_image)
                        .into(holder?.image)

            } else {
                holder?.image?.setImageResource(R.drawable.no_image)
            }
        }

    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView as ImageView

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }

}