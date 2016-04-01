/*****************************************************
 *
 * ImageProcessingService.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.image;


///// Import(s) /////

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;


///// Class Declaration /////

/*****************************************************
 *
 * This is the service that performs image processing on
 * behalf of SDK components. It should be run it a separate
 * process so that it is less likely to run out of memory
 * when performing processing tasks, and thus ensures
 * that processed images are as high a quality as possible.
 *
 * It is implemented using a messenger and handler, so that
 * it may be run in a separate process.
 *
 *****************************************************/
public class ImageProcessingService extends Service
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG      = "ImageProcessingService";

  static private final int     CROP_MESSAGE = 23;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private RequestHandler  mRequestHandler;
  private Messenger       mMessenger;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Service Method(s) //////////

  /*****************************************************
   *
   * Called when the service is created.
   *
   *****************************************************/
  @Override
  public void onCreate()
    {
    super.onCreate();

    mRequestHandler = new RequestHandler();
    mMessenger      = new Messenger( mRequestHandler );
    }


  /*****************************************************
   *
   * Called when a client binds to the service.
   *
   *****************************************************/
  @Nullable
  @Override
  public IBinder onBind( Intent intent )
    {
    return ( mMessenger.getBinder() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A handler for incoming messages.
   *
   *****************************************************/
  private class RequestHandler extends Handler
    {
    @Override
    public void handleMessage( Message message )
      {
      switch ( message.what )
        {
        case CROP_MESSAGE:
          break;

        default:
          super.handleMessage( message );
        }
      }
    }

  }

