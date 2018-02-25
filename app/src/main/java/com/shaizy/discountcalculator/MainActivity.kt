package com.shaizy.discountcalculator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    companion object {
        private const val RESULT_LOAD_IMG = 1010
        private const val REQUEST_PERMISSION = 1001

        private const val KEY_JSON_DATA =  "keyJson"
    }

    private val mList = arrayListOf<Info>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION)

            return
        }

        init()
    }

    private fun init() {
        setContentView(R.layout.activity_main)

        val data = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_JSON_DATA, "")

        mList.clear()

        if (data.isNotBlank()){
            val gson = Gson()
            val type = object: TypeToken<List<Info>>(){}.type
            val list = gson.fromJson<List<Info>>(data, type)

            mList.addAll(list)
        } else {
            mList.add(Info())
        }

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, mList)

        btnAdd.setOnClickListener {
            mList.add(Info())
            viewPager.adapter?.notifyDataSetChanged()
            viewPager.currentItem = mList.size - 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val gson = Gson()
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(KEY_JSON_DATA, gson.toJson(mList))
                .apply()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init()
        } else {
            finish()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_photo -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"

                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
            }
            R.id.action_gallery -> {
                val frag = GridFragment.newInstance(mList)
                frag.onItemClick = {
                    viewPager.currentItem = it
                    frag.dismiss()
                }

                frag.show(supportFragmentManager, null)
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                        .setMessage("Do you want to delete this?")
                        .setPositiveButton("OK", { _, _ ->
                            if (mList.size > 1) {
                                mList.remove(mList[viewPager.currentItem])
                                viewPager.adapter?.notifyDataSetChanged()
                            } else
                                Toast.makeText(this, "Not enough elements to delete", Toast.LENGTH_LONG).show()
                        })
                        .setNegativeButton("Cancel", null)
                        .show()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data?.data

                val pos = viewPager.currentItem

                mList[pos].uri = imageUri?.toString() ?: ""
                viewPager.adapter?.notifyDataSetChanged()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

}