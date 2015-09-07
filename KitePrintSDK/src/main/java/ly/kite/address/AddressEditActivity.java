package ly.kite.address;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

import ly.kite.R;
import ly.kite.journey.AKiteActivity;

public class AddressEditActivity extends AKiteActivity implements View.OnClickListener
  {

  public static final String EXTRA_ADDRESS = "ly.kite.EXTRA_ADDRESS";

  private Address address;


  private EditText  mRecipientNameEditText;
  private EditText  mAddressLine1EditText;
  private EditText  mAddressLine2EditText;
  private EditText  mAddressCityEditText;
  private EditText  mAddressCountyEditText;
  private EditText  mAddressPostcodeEditText;
  private Button    mProceedButton;


  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    setContentView( R.layout.screen_address_edit );

    mRecipientNameEditText   = (EditText)findViewById( R.id.edit_text_recipient_name   );
    mAddressLine1EditText    = (EditText)findViewById( R.id.edit_text_address_line1    );
    mAddressLine2EditText    = (EditText)findViewById( R.id.edit_text_address_line2    );
    mAddressCityEditText     = (EditText)findViewById( R.id.edit_text_address_city     );
    mAddressCountyEditText   = (EditText)findViewById( R.id.edit_text_address_county   );
    mAddressPostcodeEditText = (EditText)findViewById( R.id.edit_text_address_postcode );

    mProceedButton           = (Button)findViewById( R.id.proceed_overlay_button );


    address = getIntent().getParcelableExtra( EXTRA_ADDRESS );

    if ( address != null )
      {
      setTitle( R.string.title_activity_address_edit );
      }
    else
      {
      setTitle( R.string.manual_add_address );

      address = new Address();
      address.setCountry( Country.getInstance( Locale.getDefault() ) );
      }


    mRecipientNameEditText.setText( address.getRecipientName() );
    mAddressLine1EditText.setText( address.getLine1() );
    mAddressLine2EditText.setText( address.getLine2() );
    mAddressCityEditText.setText( address.getCity() );
    mAddressCountyEditText.setText( address.getStateOrCounty() );
    mAddressPostcodeEditText.setText( address.getZipOrPostalCode() );


    final Country[] countries = Country.values();
    int selected = address.getCountry().ordinal();
    Spinner spinner = (Spinner)findViewById( R.id.spinner_country );
    spinner.setAdapter( new ArrayAdapter<Country>( this, android.R.layout.simple_spinner_dropdown_item, countries ) );
    spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
    {
    @Override
    public void onItemSelected( AdapterView<?> adapterView, View view, int position, long i )
      {
      if ( i >= 0 && i < countries.length )
        {
        Country c = countries[ (int) i ];
        address.setCountry( c );
        }
      }

    @Override
    public void onNothingSelected( AdapterView<?> adapterView )
      {
      }
    } );

    spinner.setSelection( selected );


    mProceedButton.setText( R.string.address_edit_proceed_button_text );


    // hide keyboard initially
    this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );


    mProceedButton.setOnClickListener( this );
    }


  @Override
  public boolean onCreateOptionsMenu( Menu menu )
    {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate( R.menu.address_edit, menu );
    return true;
    }

  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if ( id == R.id.action_save )
      {
      onSaveClicked();

      return true;
      }
    else if ( id == android.R.id.home )
      {
      finish();
      return true;
      }
    return super.onOptionsItemSelected( item );
    }


  @Override
  public void onClick( View view )
    {
    if ( view == mProceedButton )
      {
      onSaveClicked();
      }
    }


  public void onSaveClicked()
    {

    String recipient = mRecipientNameEditText.getText().toString();
    if ( recipient.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_recipient, R.string.OK, null, 0, null );
      return;
      }

    String line1 = mAddressLine1EditText.getText().toString();
    if ( line1.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_line1, R.string.OK, null, 0, null );
      return;
      }

    String postalCode = mAddressPostcodeEditText.getText().toString();
    if ( postalCode.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_postal_code, R.string.OK, null, 0, null );
      return;
      }

    address.setRecipientName  ( recipient );
    address.setLine1          ( line1 );
    address.setLine2          ( mAddressLine2EditText.getText().toString() );
    address.setCity           ( mAddressCityEditText.getText().toString() );
    address.setStateOrCounty  ( mAddressCountyEditText.getText().toString() );
    address.setZipOrPostalCode( postalCode );

    Intent output = new Intent();
    output.putExtra( EXTRA_ADDRESS, (Parcelable) address );
    setResult( RESULT_OK, output );
    finish();
    }

  }
