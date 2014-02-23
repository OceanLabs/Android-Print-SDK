package co.oceanlabs.pssdk.address;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;
import java.util.Locale;

import co.oceanlabs.pssdk.R;

public class AddressEditActivity extends Activity {

    public static final String EXTRA_ADDRESS = "co.oceanlabs.pssdk.EXTRA_ADDRESS";

    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        address = getIntent().getParcelableExtra(EXTRA_ADDRESS);
        if (address == null) {
            address = new Address();
            address.setCountry(Country.getInstance(Locale.getDefault()));
            setTitle(R.string.manual_add_address);
        } else {
            setTitle(R.string.title_activity_address_edit);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(address))
                    .commit();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.address_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            onButtonSaveClicked(null);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonSaveClicked(View view) {
        address.setRecipientName(((EditText) findViewById(R.id.edit_text_recipient_name)).getText().toString());
        address.setLine1(((EditText) findViewById(R.id.edit_text_address_line1)).getText().toString());
        address.setLine2(((EditText) findViewById(R.id.edit_text_address_line2)).getText().toString());
        address.setCity(((EditText) findViewById(R.id.edit_text_address_city)).getText().toString());
        address.setStateOrCounty(((EditText) findViewById(R.id.edit_text_address_county)).getText().toString());
        address.setZipOrPostalCode(((EditText) findViewById(R.id.edit_text_address_postcode)).getText().toString());

        Intent output = new Intent();
        output.putExtra(EXTRA_ADDRESS, (Parcelable) address);
        setResult(RESULT_OK, output);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private final Address address;

        public PlaceholderFragment(Address address) {
            this.address = address;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_address_edit, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ((EditText) view.findViewById(R.id.edit_text_recipient_name)).setText(address.getRecipientName());
            ((EditText) view.findViewById(R.id.edit_text_address_line1)).setText(address.getLine1());
            ((EditText) view.findViewById(R.id.edit_text_address_line2)).setText(address.getLine2());
            ((EditText) view.findViewById(R.id.edit_text_address_city)).setText(address.getCity());
            ((EditText) view.findViewById(R.id.edit_text_address_county)).setText(address.getStateOrCounty());
            ((EditText) view.findViewById(R.id.edit_text_address_postcode)).setText(address.getZipOrPostalCode());

            final List<Country> countries = Country.COUNTRIES;
            int selected = countries.indexOf(address.getCountry());
            Spinner spinner = (Spinner) view.findViewById(R.id.spinner_country);
            spinner.setAdapter(new ArrayAdapter<Country>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countries));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long i) {
                    if (i >= 0 && i < countries.size()) {
                        Country c = countries.get((int) i);
                        address.setCountry(c);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });

            spinner.setSelection(selected);
        }
    }

}
