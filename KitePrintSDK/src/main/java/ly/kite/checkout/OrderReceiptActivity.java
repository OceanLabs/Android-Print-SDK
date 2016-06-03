package ly.kite.checkout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import ly.kite.ordering.Order;
import ly.kite.R;

public class OrderReceiptActivity extends AReceiptActivity
  {
  static public void startForResult( Activity activity, Order printOrder, int requestCode )
    {
    Intent intent = new Intent( activity, OrderReceiptActivity.class );

    addExtra( printOrder, intent );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Displays the success screen.
   *
   *****************************************************/
  @Override
  protected void onShowReceiptSuccess()
    {
    setContentView( R.layout.screen_order_receipt );

    setDisplayActionBarHomeAsUpEnabled( false );
    }


  /*****************************************************
   *
   * Displays the failure.
   *
   *****************************************************/
  @Override
  protected void onShowReceiptFailure()
    {
    setContentView( R.layout.screen_order_failure );

    setDisplayActionBarHomeAsUpEnabled( true );

    if ( mOrder.getLastPrintSubmissionError() != null )
      {
      showErrorDialog( mOrder.getLastPrintSubmissionError().getMessage() );
      }
    }


  /*****************************************************
   *
   * Called when the next button is clicked.
   *
   *****************************************************/
  @Override
  protected void onNext()
    {
    continueShopping();
    }

  }
