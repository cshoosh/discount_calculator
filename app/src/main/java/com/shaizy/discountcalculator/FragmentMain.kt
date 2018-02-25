package com.shaizy.discountcalculator

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_client.*


/**
 * Created by syedshahnawazali on 24/02/2018.
 *
 */


class FragmentMain : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_client, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { arguments ->
            val info = arguments.getParcelable<Info>(KEY_INFO)

            edtClientPrice.addTextChangedListener(Watcher(TYPE_CLIENT, info))
            edtRetail.addTextChangedListener(Watcher(TYPE_RETIAL, info))
            edtPercentActual.addTextChangedListener(Watcher(TYPE_DISCOUNT, info))
            edtPercentGiven.addTextChangedListener(Watcher(TYPE_DISCOUNT_CLIENT, info))
            edtDetails.addTextChangedListener(object : SimpleTextWatcher(){
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    info.details = s?.toString() ?: ""
                }
            })


            edtPercentActual.setText(info.discountStore.toString())
            if (info.discountGiven != 0)
                edtPercentGiven.setText(info.discountGiven.toString())

            edtRetail.setText(info.retail.toString())

            if (info.client != 0)
                edtClientPrice.setText(info.client.toString())

            edtDetails.setText(info.details)
            setImage(info.uri)
        }
    }

    private fun setImage(uri: String) {
        if (!uri.isBlank()) {
            val imageUri = Uri.parse(uri)
            Picasso.with(context)
                    .load(imageUri)
                    .error(R.drawable.no_image)
                    .into(image)
        }
    }

    inner class Watcher(private val type: Int, private val info: Info) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val price = try {
                edtRetail.text.toString().toFloat()
            } catch (e: Exception) {
                0f
            }


            val discounted = try {
                price * edtPercentActual.text.toString().toFloat() / 100f
            } catch (e: Exception) {
                price
            }

            val totalDiscount = (price - discounted).toInt()
            val thisValue = try {
                p0?.toString()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }

            when (type) {
                TYPE_RETIAL -> {
                    val discountClient = try {
                        val discountedClient = price * edtPercentGiven.text.toString().toFloat() / 100f
                        price - discountedClient
                    } catch (e: Exception) {
                        price
                    }

                    info.retail = thisValue

                    txtDiscount.text = "After Discount : $totalDiscount"
                    txtTotalDiscount.text = "Discount Total : ${discounted.toInt()}"
                    edtClientPrice.setText("${discountClient.toInt()}")
                }

                TYPE_DISCOUNT_CLIENT -> {
                    info.discountGiven = thisValue
                    edtRetail.setText(edtRetail.text.toString())
                }
                TYPE_CLIENT -> {
                    info.client = thisValue
                    val clientsProfit = thisValue - totalDiscount

                    txtProfit.text = "My Profit : ${clientsProfit.toInt()}"
                }
                TYPE_DISCOUNT -> {
                    info.discountStore = thisValue
                    val value = Math.ceil(
                            (try {
                                p0?.toString()?.toDouble() ?: 0.0
                            } catch (e: Exception) {
                                0.0
                            }) / 2.0
                    ).toString()

                    edtPercentGiven.setText(value)
                }
            }


        }

    }

    open class SimpleTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    companion object {
        private const val TYPE_RETIAL = 0
        private const val TYPE_CLIENT = 1
        private const val TYPE_DISCOUNT = 2
        private const val TYPE_DISCOUNT_CLIENT = 3

        private const val KEY_INFO = "keyInfo"

        fun newInstance(info: Info): FragmentMain {
            val bundle = Bundle()
            val frag = FragmentMain()

            bundle.putParcelable(KEY_INFO, info)

            frag.arguments = bundle

            return frag
        }
    }

}