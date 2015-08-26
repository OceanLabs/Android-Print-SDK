package ly.kite.analytics;

import android.content.Context;
import android.util.Log;

import ly.kite.product.PrintOrder;
import ly.kite.product.Product;

/**
 * Created by deon on 26/08/15.
 */
public class NullAnalyticsEventCallback implements IAnalyticsEventCallback
  {

  private final Context mContext;

  public NullAnalyticsEventCallback( Context context )
      {
        this.mContext = context;
      }

  @Override
  public void onSDKLoaded(String entryPoint) {}

  @Override
  public void onProductSelectionScreenViewed() {}

  @Override
  public void onProductOverviewScreenViewed(Product product) {}

  @Override
  public void onCreateProductScreenViewed(Product product) {}

  @Override
  public void onShippingScreenViewed(PrintOrder printOrder, String variant, boolean showPhoneEntryField) {}

  @Override
  public void onPaymentScreenViewed(PrintOrder printOrder) {}

  @Override
  public void onPaymentCompleted(PrintOrder printOrder, String paymentMethod) {}

  @Override
  public void onOrderSubmission(PrintOrder printOrder) {}
  }
