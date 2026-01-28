package com.justuju.shred;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.justuju.shred.Utils.AppConstants;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    AppConstants appConstants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        setTitle("App Settings");

         appConstants = AppConstants.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem homeButton = menu.findItem(R.id.action_home);
        MenuItem settingButton = menu.findItem(R.id.action_settings);
        homeButton.setVisible(false); //hide home action icon
        settingButton.setVisible(false); // since we already in setting activity, no need to show setting icon in actionbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectAllItem(MenuItem selectAll) {

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference prefListLanguages = (ListPreference) findPreference("language");

            if (prefListLanguages != null) {
                prefListLanguages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        setLocale(getActivity(), newValue.toString());
                        Toast.makeText(getContext(), newValue.toString(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }

        }

        public void setLocale(Activity context, String langCode) {
            Locale myLocale = new Locale(langCode);
            Resources res = getActivity().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
//            Intent refresh = new Intent(SettingsActivity.this, SettingsActivity.class);
//            finish();
//            startActivity(refresh);
        }
    }


}