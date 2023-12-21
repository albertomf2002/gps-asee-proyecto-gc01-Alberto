package es.unex.giiis.asee.totalemergency.view.home

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.unex.giiis.asee.totalemergency.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}