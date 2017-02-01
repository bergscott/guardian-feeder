package com.bergscott.android.guardianfeeder;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class ArticlePreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // find the preference for order by and set up its summary
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            // find the preference for max articles and set up its summary
            Preference maxArticles = findPreference(getString(R.string.settings_max_articles_key));
            bindPreferenceSummaryToValue(maxArticles);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            // handle list preferences
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            // for the max articles setting, make sure it is within 1-50
            } else if (TextUtils.equals(preference.getKey(), getString(R.string.settings_max_articles_key))) {
                int intValue = Integer.valueOf(stringValue);
                if (intValue >= 1 && intValue <= 50) {
                    preference.setSummary(stringValue);
                } else {
                    // if outside range, reject the preference change
                    Toast.makeText(getActivity(), "Max Articles must be within 1 and 50", Toast.LENGTH_SHORT).show();
                    return false;
                }
            // for all other settings set the setting to the string value
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
