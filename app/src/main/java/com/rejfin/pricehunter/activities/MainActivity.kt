package com.rejfin.pricehunter.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.rejfin.pricehunter.R
import com.rejfin.pricehunter.fragments.ConfigFileFragment
import com.rejfin.pricehunter.fragments.HomeFragment
import com.rejfin.pricehunter.fragments.SettingsFragment

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create toolbar options
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.menu.add(0, 1, 0, R.string.settings)
        toolbar.menu.getItem(0).apply{
            icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_settings)
            setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }

        toolbar.setOnMenuItemClickListener { item->
            if(item.itemId == 1){
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .addToBackStack("home")
                    .replace(R.id.fragment_container, SettingsFragment())
                    .commit()
            }
            true
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        // check if config values exist
        if(pref.contains("firebase_config")){
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, HomeFragment(), "home")
                .commit()
        }else{
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, ConfigFileFragment())
                .commit()
        }
    }
}