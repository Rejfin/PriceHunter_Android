package com.rejfin.pricehunter.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rejfin.pricehunter.BuildConfig
import com.rejfin.pricehunter.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.menu.getItem(0).isVisible = false

        // find right properties //
        val aboutPref = findPreference<Preference>("about")
        val loadFilePref = findPreference<Preference>("load_file")

        // set summary in 'About' and 'Root' preferences //
        aboutPref?.summary = "PriceHunter v${BuildConfig.VERSION_NAME} by Rejfin"

        loadFilePref?.setOnPreferenceClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack("settings")
                .replace(R.id.fragment_container, ConfigFileFragment())
                .commit()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}