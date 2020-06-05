package tk.paulmburu.treemap.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.utils.ThemeHelper

class ThemeSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val TAG = "ThemeSettingsFragment"
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val themePreference =
            findPreference<ListPreference>("themePref")
        if (themePreference != null) {
            themePreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val themeOption = newValue as String
                    ThemeHelper.applyTheme(themeOption)
                    true
                }
        }
    }

}