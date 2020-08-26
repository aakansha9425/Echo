package com.example.echo2.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.example.echo2.R

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {
    var myactivity: Activity? = null
    var shakeswitch: Switch? = null

    object Statified {
        var MY_PREFS_NAME = "shake feature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        shakeswitch = view?.findViewById(R.id.switchshake)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isallowed = prefs?.getBoolean("feature", false)
        if (isallowed as Boolean) {
            shakeswitch?.isChecked = true
        } else {
            shakeswitch?.isChecked = false
        }
        shakeswitch?.setOnCheckedChangeListener({ CompoundButton, b ->
            if (b) {
                val editor =
                    myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                        ?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor =
                    myactivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                        ?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }

        })

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }


}
