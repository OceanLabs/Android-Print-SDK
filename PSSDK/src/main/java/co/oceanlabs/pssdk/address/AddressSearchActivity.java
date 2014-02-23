package co.oceanlabs.pssdk.address;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.oceanlabs.pssdk.R;

public class AddressSearchActivity extends Activity implements ActionBar.OnNavigationListener, AddressSearchRequestListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private SearchView searchView;
    private AddressSearchRequest inProgressAddressSearchReq;

    private static final int REQUEST_CODE_ADDRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<Country>(
                        this,
                        R.layout.country_spinner_item,
                        android.R.id.text1,
                        Country.COUNTRIES),
                this);

        int selected = Country.COUNTRIES.indexOf(Country.getInstance(Locale.getDefault()));
        actionBar.setSelectedNavigationItem(selected);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onNavigationItemSelected(int i, long position) {
        Country selectedCountry = Country.COUNTRIES.get((int) position);
        searchView.setQueryHint("Search " + selectedCountry.getName());
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADDRESS && resultCode == RESULT_OK) {
            // pass result back to AddressBook.
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.address_search, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        Country c = Country.COUNTRIES.get(getActionBar().getSelectedNavigationIndex());
        searchView.setQueryHint("Search " + c.getName());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.i("pssdk2", "search for " + query);
                if (inProgressAddressSearchReq != null) {
                    inProgressAddressSearchReq.cancelSearch();
                    inProgressAddressSearchReq = null;
                }

                if (query.trim().length() == 0) {
                    // clear results
                    ListView addressSearchResults = (ListView) findViewById(R.id.list_view_address_search_results);
                    AddressSearchResultAdapter adapter = (AddressSearchResultAdapter) addressSearchResults.getAdapter();
                    adapter.setAddresses(null);
                    return true;
                }

                inProgressAddressSearchReq = new AddressSearchRequest();
                Country c = Country.COUNTRIES.get(getActionBar().getSelectedNavigationIndex());
                inProgressAddressSearchReq.search(query, c, AddressSearchActivity.this);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchForAddressDetails(Address address) {
        if (inProgressAddressSearchReq != null) {
            inProgressAddressSearchReq.cancelSearch();
            inProgressAddressSearchReq = null;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching Details");
        dialog.setMessage("Fetching address details");
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (inProgressAddressSearchReq != null) {
                    inProgressAddressSearchReq.cancelSearch();
                    inProgressAddressSearchReq = null;
                }
            }
        });

        inProgressAddressSearchReq = new AddressSearchRequest();
        inProgressAddressSearchReq.searchForAddress(address, new AddressSearchRequestListener() {
            @Override
            public void onMultipleChoices(AddressSearchRequest req, List<Address> options) {
                dialog.dismiss();
                AddressSearchActivity.this.onMultipleChoices(req, options);
            }

            @Override
            public void onUniqueAddress(AddressSearchRequest req, Address address) {
                dialog.dismiss();
                Intent intent = new Intent(AddressSearchActivity.this, AddressEditActivity.class);
                intent.putExtra(AddressEditActivity.EXTRA_ADDRESS, (Parcelable) address);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS);
            }

            @Override
            public void onError(AddressSearchRequest req, Exception error) {
                dialog.dismiss();
                AddressSearchActivity.this.onError(req, error);
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_address_search, container, false);
            final ListView addressSearchResults = (ListView) rootView.findViewById(R.id.list_view_address_search_results);
            addressSearchResults.setAdapter(new AddressSearchResultAdapter());

            addressSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
                    Address addr = (Address) addressSearchResults.getAdapter().getItem((int) position);
                    if (addr.isSearchRequiredForFullDetails()) {
                        ((AddressSearchActivity) getActivity()).searchForAddressDetails(addr);
                    } else {
                        Intent intent = new Intent(getActivity(), AddressEditActivity.class);
                        intent.putExtra(AddressEditActivity.EXTRA_ADDRESS, (Parcelable) addr);
                        startActivityForResult(intent, REQUEST_CODE_ADDRESS);
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            ((AddressSearchActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onMultipleChoices(AddressSearchRequest req, List<Address> options) {
        ListView addressSearchResults = (ListView) findViewById(R.id.list_view_address_search_results);
        AddressSearchResultAdapter adapter = (AddressSearchResultAdapter) addressSearchResults.getAdapter();
        adapter.setAddresses(options);
    }

    public void onUniqueAddress(AddressSearchRequest req, Address address) {
        ArrayList<Address> addresses = new ArrayList<Address>();
        addresses.add(address);
        onMultipleChoices(req, addresses);
    }

    public void onError(AddressSearchRequest req, Exception error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!").setMessage(error.getMessage()).setPositiveButton("OK", null);
        Dialog d = builder.create();
        d.show();
    }

    private static class AddressSearchResultAdapter extends BaseAdapter {

        private List<Address> addresses;

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return addresses == null ? 0 : addresses.size();
        }

        @Override
        public Object getItem(int i) {
            return addresses.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.address_search_result_list_item, null);
            }

            Address a = (Address) getItem(position);
            ((TextView) v.findViewById(android.R.id.text1)).setText(a.toString());
            return v;
        }
    }

}
