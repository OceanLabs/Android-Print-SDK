package ly.kite.facebookphotopicker;

///// Import(s) /////

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.util.Asset;
import ly.kite.facebookphotopicker.FacebookPhotoPicker;
import ly.kite.journey.AImageSource;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a local device image source.
 *
 *****************************************************/
public class FacebookImageSource extends AImageSource
{
    ////////// Static Constant(s) //////////

    @SuppressWarnings( "unused" )
    static private final String  LOG_TAG = "FacebookImageSource";


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////


    ////////// Constructor(s) //////////

    public FacebookImageSource()
    {
        super( R.color.image_source_background_facebook,
            R.drawable.ic_image_source_facebook,
            R.string.kitesdk_image_source_facebook,
            R.id.add_image_from_facebook,
            R.string.kitesdk_select_photo_from_facebook);
    }


    ////////// AImageSource Method(s) //////////

    /*****************************************************
     *
     * Returns true if this source is available.
     *
     *****************************************************/
    public boolean isAvailable( Context context )
    {
        return ( KiteSDK.getInstance( context ).haveFacebookCredentials() );
    }


    /*****************************************************
     *
     * Called when the image source is picked to select
     * images.
     *
     *****************************************************/
    public void onPick( Fragment fragment, int addedAssetCount, boolean supportsMultiplePacks, int packSize, int maxImageCount )
    {
        FacebookPhotoPicker.startPhotoPickerForResult( fragment, addedAssetCount, supportsMultiplePacks, packSize, maxImageCount, getActivityRequestCode() );
    }


    /*****************************************************
     *
     * Calls back with any picked images.
     *
     *****************************************************/
    @Override
    public void getAssetsFromPickerResult( Activity activity, Intent data, IAssetConsumer assetConsumer )
    {
        List<String> photoURLStringList = FacebookPhotoPicker.getResultPhotos( data );

        if ( photoURLStringList != null )
        {
            // Create an asset list, populate it, and call back to the consumer immediately.

            List<Asset> assetList = new ArrayList<>( photoURLStringList.size() );

            for ( String urlString : photoURLStringList )
            {
                try
                {
                    assetList.add( new Asset( new URL( urlString ) ) );
                }
                catch ( MalformedURLException mue )
                {
                    Log.e( LOG_TAG, "Unable to create asset from Facebook photo URL: " + urlString, mue );
                }
            }

            assetConsumer.isacOnAssets( assetList );
        }
    }


    /*****************************************************
     *
     * Called to end the customer session.
     *
     *****************************************************/
    @Override
    public void endCustomerSession( Context context )
    {
        FacebookPhotoPicker.endCustomerSession();
    }


    ////////// Inner Class(es) //////////

}


