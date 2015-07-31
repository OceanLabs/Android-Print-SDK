/*****************************************************
 *
 * AddressBookActivity.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.address;


///// Import(s) /////

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ly.kite.R;
import ly.kite.journey.AKiteActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This acitivyt displays the known mAddressList, and permits
 * the user to edit/delete them or add new ones.
 *
 *****************************************************/
public class AddressBookActivity extends AKiteActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                 = "AddressBookActivity";

  public  static final String EXTRA_ADDRESS            = "ly.kite.EXTRA_ADDRESS";

  private static final int    REQUEST_CODE_ADD_ADDRESS = 0;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ListView                mAddressBookListView;
  private TextView                mEmptyMessageTextView;

  private AddressBookListAdaptor  mAddressBookListAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    setContentView( R.layout.activity_address_book );

    mAddressBookListView  = (ListView)findViewById( R.id.address_book_list_view );
    mEmptyMessageTextView = (TextView)findViewById( R.id.empty_message_text_view );


    ActionBar actionBar = getActionBar();

    if ( actionBar != null )
      {
      actionBar.setDisplayHomeAsUpEnabled( true );
      }


    updateScreen();
    }


  /*****************************************************
   *
   * Called when the options menu is created.
   *
   *****************************************************/
  @Override
  public boolean onCreateOptionsMenu( Menu menu )
    {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.address_book, menu );

    return ( true );
    }


  /*****************************************************
   *
   * Called when a menu item (action) is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    int id = item.getItemId();

    if ( id == android.R.id.home )
      {
      finish();

      return ( true );
      }
    else if ( id == R.id.search_for_address )
      {
      startActivityForResult( new Intent( this, AddressSearchActivity.class ), REQUEST_CODE_ADD_ADDRESS );

      return ( true );
      }
    else if ( id == R.id.manual_add_address )
      {
      startActivityForResult( new Intent( this, AddressEditActivity.class ), REQUEST_CODE_ADD_ADDRESS );

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when one of the activities returns a result.
   *
   *****************************************************/
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_ADD_ADDRESS && resultCode == RESULT_OK )
      {
      Address address = data.getParcelableExtra( AddressEditActivity.EXTRA_ADDRESS );

      address.saveToAddressBook( this );

      updateScreen();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Updates the screen.
   *
   *****************************************************/
  private void updateScreen()
    {
    mAddressBookListAdaptor = new AddressBookListAdaptor( this );
    mAddressBookListView.setAdapter( mAddressBookListAdaptor );

    if ( mAddressBookListAdaptor.getCount() > 0 ) mEmptyMessageTextView.setVisibility( View.GONE );
    else                                          mEmptyMessageTextView.setVisibility( View.VISIBLE );


    mAddressBookListView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener()
      {
      @Override
      public boolean onItemLongClick( AdapterView<?> adapterView, View view, int i, long position )
        {
        final Address address = (Address) mAddressBookListAdaptor.getItem( (int) position );
        AlertDialog.Builder builder = new AlertDialog.Builder( AddressBookActivity.this );
        builder.setTitle( address.toString() )
                .setItems( new String[]{ "Edit Address", "Delete Address" }, new DialogInterface.OnClickListener()
                {
                @Override
                public void onClick( DialogInterface dialogInterface, int i )
                  {
                  if ( i == 0 )
                    {
                    Intent intent = new Intent( AddressBookActivity.this, AddressEditActivity.class );
                    intent.putExtra( AddressEditActivity.EXTRA_ADDRESS, (Parcelable) address );
                    startActivityForResult( intent, REQUEST_CODE_ADD_ADDRESS );
                    }
                  else if ( i == 1 )
                    {
                    address.deleteFromAddressBook( AddressBookActivity.this );

                    updateScreen();
                    }
                  }
                } );
        builder.create().show();
        return true;
        }
      } );


    mAddressBookListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
      {
      @Override
      public void onItemClick( AdapterView<?> adapterView, View view, int i, long position )
        {
        // Return the selected address back to the calling activity

        Address selectedAddress = (Address)mAddressBookListAdaptor.getItem( (int) position );

        Intent data = new Intent();
        data.putExtra( EXTRA_ADDRESS, (Parcelable)selectedAddress );
        setResult( Activity.RESULT_OK, data );

        finish();
        }
      } );

    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An adaptor for the addresses.
   *
   *****************************************************/
  private static class AddressBookListAdaptor extends BaseAdapter
    {

    private List<Address> mAddressList;

    AddressBookListAdaptor( Context context )
      {
      mAddressList = Address.getAddressBook( context );
      }

    @Override
    public int getCount()
      {
      return mAddressList.size();
      }

    @Override
    public Object getItem( int i )
      {
      return mAddressList.get( i );
      }

    @Override
    public long getItemId( int i )
      {
      return i;
      }

    @Override
    public View getView( int position, View convertView, ViewGroup viewGroup )
      {
      View v = convertView;
      if ( convertView == null )
        {
        LayoutInflater li = (LayoutInflater) viewGroup.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        v = li.inflate( R.layout.address_book_list_item, null );
        }

      Address a = (Address) getItem( position );
      ((TextView) v.findViewById( android.R.id.text1 )).setText( a.getRecipientName() );
      ((TextView) v.findViewById( android.R.id.text2 )).setText( a.getDisplayAddressWithoutRecipient() );


      return v;
      }
    }


  }
