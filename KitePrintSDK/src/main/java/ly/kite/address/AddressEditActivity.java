package ly.kite.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import ly.kite.KiteSDK;
import ly.kite.R;

public class AddressEditActivity extends AAddressActivity implements View.OnClickListener
  {
  static private  final String  KEY_ADDRESS_READ_ONLY     = "ly.kite.addressreadonly";
  static private  final String  KEY_REQUEST_EMAIL_ADDRESS = "ly.kite.requestemailaddress";


  private Address  mAddress;
  private boolean  mRequestEmailAddress;


  private EditText  mRecipientNameEditText;
  private EditText  mAddressLine1EditText;
  private EditText  mAddressLine2EditText;
  private EditText  mAddressCityEditText;
  private EditText  mAddressCountyEditText;
  private EditText  mAddressPostcodeEditText;
  private EditText  mEmailAddressEditText;
  private Button    mProceedButton;



  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts the activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Address address, boolean addressReadOnly, boolean requestEmailAddress, String emailAddress, int requestCode )
    {
    Intent intent = new Intent( activity, AddressEditActivity.class );


    addAddress( address, intent );

    intent.putExtra( KEY_ADDRESS_READ_ONLY,     addressReadOnly );


    intent.putExtra( KEY_REQUEST_EMAIL_ADDRESS, requestEmailAddress );

    addEmailAddress( emailAddress, intent );


    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Starts the activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Address address, int requestCode )
    {
    startForResult( activity, address, false, false, null, requestCode );
    }


  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    Intent intent = getIntent();

    boolean addressReadOnly     = false;
    String  emailAddress        = null;

    if ( intent != null )
      {
      mAddress             = getAddress( intent );
      addressReadOnly      = intent.getBooleanExtra( KEY_ADDRESS_READ_ONLY,     false );

      mRequestEmailAddress = intent.getBooleanExtra( KEY_REQUEST_EMAIL_ADDRESS, false );
      emailAddress         = getEmailAddress( intent );
      }


    setContentView( R.layout.screen_address_edit );

    mRecipientNameEditText   = (EditText)findViewById( R.id.edit_text_recipient_name   );
    mAddressLine1EditText    = (EditText)findViewById( R.id.edit_text_address_line1    );
    mAddressLine2EditText    = (EditText)findViewById( R.id.edit_text_address_line2    );
    mAddressCityEditText     = (EditText)findViewById( R.id.edit_text_address_city     );
    mAddressCountyEditText   = (EditText)findViewById( R.id.edit_text_address_county   );
    mAddressPostcodeEditText = (EditText)findViewById( R.id.edit_text_address_postcode );
    mEmailAddressEditText    = (EditText)findViewById( R.id.edit_text_email_address    );

    mProceedButton           = (Button)findViewById( R.id.proceed_overlay_button );


    if ( mAddress != null )
      {
      setTitle( R.string.title_activity_address_edit );
      }
    else
      {
      setTitle( R.string.manual_add_address );

      mAddress = new Address();
      mAddress.setCountry( Country.getInstance( Locale.getDefault() ) );
      }


    mRecipientNameEditText.setText( mAddress.getRecipientName() );
    mAddressLine1EditText.setText( mAddress.getLine1() );
    mAddressLine2EditText.setText( mAddress.getLine2() );
    mAddressCityEditText.setText( mAddress.getCity() );
    mAddressCountyEditText.setText( mAddress.getStateOrCounty() );
    mAddressPostcodeEditText.setText( mAddress.getZipOrPostalCode() );


    // If there is an email address text field - set it up according to whether we want
    // the user to enter an email address.

    if ( mEmailAddressEditText != null )
      {
      if ( mRequestEmailAddress )
        {
        mEmailAddressEditText.setVisibility( View.VISIBLE );
        mEmailAddressEditText.setText( emailAddress );
        }
      else
        {
        mEmailAddressEditText.setVisibility( View.GONE );
        }
      }


    final Country[] countries = Country.values();
    int selected = mAddress.getCountry().ordinal();
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
        mAddress.setCountry( c );
        }
      }

    @Override
    public void onNothingSelected( AdapterView<?> adapterView )
      {
      }
    } );

    spinner.setSelection( selected );


    if ( addressReadOnly )
      {
      mAddressLine1EditText.setEnabled( false );
      mAddressLine2EditText.setEnabled( false );
      mAddressCityEditText.setEnabled( false );
      mAddressCountyEditText.setEnabled( false );
      mAddressPostcodeEditText.setEnabled( false );
      spinner.setEnabled( false );
      }


    mProceedButton.setText( R.string.address_edit_proceed_button_text );


    // hide keyboard initially
    this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );


    mProceedButton.setOnClickListener( this );
    }


  @Override
  public boolean onCreateOptionsMenu( Menu menu )
    {
    // We only add actions if the address book is enabled. At the moment the only
    // action is to save the address (which we won't want to do).

    if ( KiteSDK.getInstance( this ).addressBookIsEnabled() )
      {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate( R.menu.address_edit, menu );

      return ( true );
      }


    return ( super.onCreateOptionsMenu( menu ) );
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
      onCancel();

      return true;
      }

    return super.onOptionsItemSelected( item );
    }


  @Override
  public void onBackPressed()
    {
    onCancel();
    }


  @Override
  public void onClick( View view )
    {
    if ( view == mProceedButton )
      {
      onSaveClicked();

      return;
      }

    super.onClick( view );
    }


  public void onSaveClicked()
    {
    // Get and verify the recipient name

    String recipient = mRecipientNameEditText.getText().toString();

    if ( recipient.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_recipient, R.string.OK, null, 0, null );

      return;
      }


    // Get and verify the first address line

    String line1 = mAddressLine1EditText.getText().toString();

    if ( line1.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_line1, R.string.OK, null, 0, null );

      return;
      }


    // Get and verify the post code

    String postalCode = mAddressPostcodeEditText.getText().toString();

    if ( postalCode.trim().length() == 0 )
      {
      displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_postal_code, R.string.OK, null, 0, null );
      return;
      }


    // If we want an email address, make sure one was entered

    String emailAddress = null;

    if ( mRequestEmailAddress && mEmailAddressEditText != null )
      {
      emailAddress = mEmailAddressEditText.getText().toString();

      if ( emailAddress.trim().length() < 1 )
        {
        displayModalDialog( R.string.alert_dialog_title_oops, R.string.alert_dialog_message_no_email_address, R.string.OK, null, NO_BUTTON, null );

        return;
        }
      }


    // Update the address
    mAddress.setRecipientName  ( recipient );
    mAddress.setLine1          ( line1 );
    mAddress.setLine2          ( mAddressLine2EditText.getText().toString() );
    mAddress.setCity           ( mAddressCityEditText.getText().toString() );
    mAddress.setStateOrCounty  ( mAddressCountyEditText.getText().toString() );
    mAddress.setZipOrPostalCode( postalCode );

    returnResult( mAddress, emailAddress );

    finish();
    }


  private void onCancel()
    {
    setResult( RESULT_CANCELED );

    finish();
    }

  }
