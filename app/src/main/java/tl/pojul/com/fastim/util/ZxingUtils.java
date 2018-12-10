package tl.pojul.com.fastim.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Hashtable;

public class ZxingUtils {

    public static Bitmap createBitmap(String str){
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        Bitmap bitmap;
        BitMatrix result;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 400, 400, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (Exception e){
            return null;
        }
        return bitmap;
    }

}
