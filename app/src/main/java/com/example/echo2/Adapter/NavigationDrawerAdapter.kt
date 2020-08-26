package com.example.echo2.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aryan.echo.fragments.AboutUsFragment
import com.example.echo2.R
import com.example.echo2.activities.MainActivity
import com.example.echo2.fragment.Faviourate
import com.example.echo2.fragment.MainScreenFragment
import com.example.echo2.fragment.SettingFragment

class NavigationDrawerAdapter(
    _contentList: ArrayList<String>,
    _getimages: IntArray,
    _context: Context
) : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList: ArrayList<String>? = null
    var getimages: IntArray? = null
    var mcontext: Context? = null

    init {
        this.contentList = _contentList
        this.getimages = _getimages
        this.mcontext = _context
    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Get_icon: ImageView? = null
        var Get_text: TextView? = null
        var Get_contentHolder: RelativeLayout? = null

        init {
            Get_icon = itemView.findViewById(R.id.icon_navigation_drawer)
            Get_text = itemView.findViewById(R.id.text_navigation_drawer)
            Get_contentHolder = itemView.findViewById(R.id.navigatoion_drawer_content_holder)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        var itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_navigation_drawer, parent, false)
        val returnThis = NavViewHolder(itemview)
        return returnThis
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.Get_icon?.setBackgroundResource(getimages?.get(position) as Int)
        holder.Get_text?.setText(contentList?.get(position))
        holder.Get_contentHolder?.setOnClickListener({
            if (position == 0) {
                val mscreen = MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_details, mscreen)
                    .commit()
            } else if (position == 1) {
                val fav = Faviourate()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_details, fav)
                    .commit()
            } else if (position == 2) {
                val setting = SettingFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_details, setting)
                    .commit()
            } else {
                val aboutus = AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_details, aboutus)
                    .commit()

            }
            MainActivity.Statified.drawerlayout?.closeDrawers()

        })
    }

}