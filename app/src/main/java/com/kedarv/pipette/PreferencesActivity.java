package com.kedarv.pipette;

/**
 * Created by kedar on 6/15/16.
 */
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
