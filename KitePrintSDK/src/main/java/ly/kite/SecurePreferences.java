package ly.kite;


/*
Copyright (C) 2012 Sveinung Kval Bakken, sveinung.bakken@gmail.com
Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:
The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SecurePreferences {

    public static class SecurePreferencesException extends RuntimeException {

        public SecurePreferencesException(Throwable e) {
            super(e);
        }

    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";

    private Cipher writer;
    private Cipher reader;

    public static boolean encryptData = true;
    private final String secureKey;

    /**
     * This will initialize an instance of the SecurePreferences class
     * @param secureKey the key used for encryption, finding a good key scheme is hard.
     * Hardcoding your key in the application is bad, but better than plaintext preferences. Having the user enter the key upon application launch is a safe(r) alternative, but annoying to the user.
     * true will encrypt both values and keys. Keys can contain a lot of information about
     * the plaintext value of the value which can be used to decipher the value.
     * @throws SecurePreferencesException
     */
    public SecurePreferences(String secureKey) throws SecurePreferencesException {
        if(secureKey.toLowerCase().equals("off"))
            encryptData = false;

        this.secureKey = secureKey;
        reset();
    }

    public void reset() {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            initCiphers(this.secureKey);
        } catch (GeneralSecurityException e) {
            throw new SecurePreferencesException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    protected void initCiphers(String secureKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
    }

    protected IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        System.arraycopy("CHANGE_ME_IF_YOU_WANT".getBytes(), 0, iv, 0, writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    protected SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, TRANSFORMATION);
    }

    protected byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }


    public String encrypt(String value) throws SecurePreferencesException {
        if(encryptData == false)
            return value;
        else {
            if (value == null)
                return null;
            byte[] secureValue;
            try {
                secureValue = convert(writer, value.getBytes(CHARSET));
            } catch (UnsupportedEncodingException e) {
                throw new SecurePreferencesException(e);
            }
            String secureValueEncoded = Base64.encodeToString(secureValue, Base64.NO_WRAP);
            return secureValueEncoded;
        }
    }

    public String decrypt(String securedEncodedValue) {
        if(encryptData==false)
            return securedEncodedValue;
        else {
            if (securedEncodedValue == null)
                return null;
            byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
            byte[] value = convert(reader, securedValue);
            try {
                return new String(value, CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new SecurePreferencesException(e);
            }
        }
    }

    private static byte[] convert(Cipher cipher, byte[] bs) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        }
        catch (Exception e) {
            throw new SecurePreferencesException(e);
        }
    }
}