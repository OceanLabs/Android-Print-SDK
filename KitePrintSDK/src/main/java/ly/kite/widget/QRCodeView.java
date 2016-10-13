/*****************************************************
 *
 * QRCodeView.java
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

package ly.kite.widget;


///// Import(s) /////

import java.net.URL;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.onbarcode.barcode.android.*;


///// Class Declaration /////

/*****************************************************
 *
 * This view displays a QR code.
 *
 *****************************************************/
public class QRCodeView extends View
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "QRCodeView";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private QRCode  mQRCode;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public QRCodeView( Context context )
    {
    super( context );
    }

  public QRCodeView( Context context, AttributeSet attrs )
    {
    super( context, attrs );
    }

  public QRCodeView( Context context, AttributeSet attrs, int defStyleAttr )
    {
    super( context, attrs, defStyleAttr );
    }


  ////////// View Method(s) //////////

  /*****************************************************
   *
   * Draws the view.
   *
   *****************************************************/
  @Override
  public void onDraw( Canvas canvas )
    {
    super.onDraw( canvas );

    if ( mQRCode != null )
      {
      int width = getWidth();
      int height = getHeight();

      mQRCode.setBarcodeWidth( height );
      mQRCode.setBarcodeHeight( height );

      int halfWidth  = width / 2;
      int halfHeight = height / 2;

      RectF rectF = new RectF( halfWidth - halfHeight, 0, halfWidth + halfHeight, height );

      try
        {
        mQRCode.drawBarcode( canvas, rectF );
        }
      catch ( Exception ignore )
        {
        }
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the URL.
   *
   *****************************************************/
  public void setURL( URL url )
    {
    mQRCode = new QRCode();

    mQRCode.setData( url.toExternalForm() );
    mQRCode.setAutoResize( true );

    invalidate();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

