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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


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

  private int     mSize;

  private URL     mURL;

  private Bitmap  mQRCodeBitmap;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates the QR code bitmap.
   *
   *****************************************************/
  static private Bitmap createQRCodeBitmap( URL url, int size )
    {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();

    try
      {
      BitMatrix bitMatrix = qrCodeWriter.encode( url.toExternalForm(), BarcodeFormat.QR_CODE, size, size, null );

      int matrixWidth = bitMatrix.getWidth();
      int matrixHeight = bitMatrix.getHeight();


      // Create a bitmap and a canvas over it

      Bitmap bitmap = Bitmap.createBitmap( matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888 );

      Canvas canvas = new Canvas( bitmap );

      Paint  paint = new Paint();

      paint.setColor( 0xff000000 );
      paint.setStyle( Paint.Style.FILL );


      // Draw the bits onto the canvas

      for ( int i = 0; i < matrixWidth; i++ )
        {
        for ( int j = 0; j < matrixHeight; j++ )
          {
          if ( bitMatrix.get( i, j ) )
            {
            canvas.drawRect( i, j, i + 1, j + 1, paint );
            }
          }
        }


      return ( bitmap );
      }
    catch ( WriterException we )
      {
      Log.e( LOG_TAG, "Unable to encode QR code for " + url.toString(), we );
      }

    return ( null );
    }


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
   * Called when the size changes.
   *
   *****************************************************/
  @Override
  public void onSizeChanged( int width, int height, int oldWidth, int oldHeight )
    {
    super.onSizeChanged( width, height, oldWidth, oldHeight );

    mSize         = Math.min( width, height );

    mQRCodeBitmap = null;

    invalidate();
    }


  /*****************************************************
   *
   * Draws the view.
   *
   *****************************************************/
  @Override
  public void onDraw( Canvas canvas )
    {
    super.onDraw( canvas );

    if ( mQRCodeBitmap == null )
      {
      mQRCodeBitmap = createQRCodeBitmap( mURL, mSize );
      }

    if ( mQRCodeBitmap != null )
      {
      int width = getWidth();
      int height = getHeight();

      int halfWidth  = width / 2;
      int halfHeight = height / 2;
      int halfSize   = mSize / 2;

      Rect  sourceRect  = new Rect( 0, 0, mQRCodeBitmap.getWidth(), mQRCodeBitmap.getHeight() );
      RectF targetRectF = new RectF( halfWidth - halfSize, halfHeight - halfSize, halfWidth + halfSize, halfHeight + halfSize );

      canvas.drawBitmap( mQRCodeBitmap, sourceRect, targetRectF, null );
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
    mURL = url;

    invalidate();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

