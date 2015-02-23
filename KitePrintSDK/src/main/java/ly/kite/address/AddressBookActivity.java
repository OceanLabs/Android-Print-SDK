package ly.kite.address;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ly.kite.R;

public class AddressBookActivity extends Activity {

    public static final String EXTRA_ADDRESS = "ly.kite.EXTRA_ADDRESS";

    private static final int REQUEST_CODE_ADD_ADDRESS = 0;
    private AddressBookAdapter addressBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        addressBookAdapter = new AddressBookAdapter();
        addressBookAdapter.setAddresses(Address.getAddressBook(this));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(addressBookAdapter))
                    .commit();
        }

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.address_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.search_for_address) {
            startActivityForResult(new Intent(this, AddressSearchActivity.class), REQUEST_CODE_ADD_ADDRESS);
            return true;
        } else if (id == R.id.manual_add_address) {
            startActivityForResult(new Intent(this, AddressEditActivity.class), REQUEST_CODE_ADD_ADDRESS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_ADDRESS && resultCode == RESULT_OK) {
            Address a = data.getParcelableExtra(AddressEditActivity.EXTRA_ADDRESS);
            a.saveToAddressBook(this);
            addressBookAdapter.setAddresses(Address.getAddressBook(this));
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private final AddressBookAdapter adapter;

        public PlaceholderFragment(AddressBookAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_address_book, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            ListView addressBookList = (ListView) view.findViewById(R.id.list_view_address_book);
            addressBookList.setAdapter(adapter);

            addressBookList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long position) {
                    final Address address = (Address) adapter.getItem((int) position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(address.toString())
                            .setItems(new String[] {"Edit Address", "Delete Address"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Intent intent = new Intent(getActivity(), AddressEditActivity.class);
                                        intent.putExtra(AddressEditActivity.EXTRA_ADDRESS, (Parcelable) address);
                                        startActivityForResult(intent, REQUEST_CODE_ADD_ADDRESS);
                                    } else if (i == 1) {
                                        address.deleteFromAddressBook(getActivity());
                                        adapter.setAddresses(Address.getAddressBook(getActivity()));
                                    }
                                }
                            });
                    builder.create().show();
                    return true;
                }
            });

            addressBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long position) {
                    Address addr = (Address) adapter.getItem((int) position);
                    Intent data = new Intent();
                    data.putExtra(EXTRA_ADDRESS, (Parcelable) addr);
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();

                }
            });

            TextView empty = (TextView) view.findViewById(R.id.empty);
            addressBookList.setEmptyView(empty);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            ((AddressBookActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
        }
    }

    private static class AddressBookAdapter extends BaseAdapter {

        private List<Address> addresses;

        public void setAddresses(List<Address> addresses) {
            this.addresses = addresses;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return addresses.size();
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
                v = li.inflate(R.layout.address_book_list_item, null);
            }

            Address a = (Address) getItem(position);
            ((TextView) v.findViewById(android.R.id.text1)).setText(a.getRecipientName());
            ((TextView) v.findViewById(android.R.id.text2)).setText(a.getDisplayAddressWithoutRecipient());


            return v;
        }
    }

}
