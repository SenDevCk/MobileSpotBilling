package org.cso.MSBUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utilities {
    public static Bitmap getBitmapFromUri(Context context,Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap getBitmapFromPath(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    public Bitmap getBitmapFromUri(Uri uri,Context context) throws FileNotFoundException, IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    public String getDrawablePath(Context context,int drawableId) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + drawableId).toString();
    }

    public static Bitmap getBitmap(File picturesDir,String pictureName){
        Bitmap bitmap;
        if (picturesDir != null && picturesDir.exists()) {
            // Get the file path of the image
            File imageFile = new File(picturesDir, pictureName); //Change "image.jpg" to your image file name
            // Check if the image file exists
            if (imageFile.exists()) {
                // Decode the image file into a Bitmap
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                // Now you can use the 'bitmap' object to do whatever you want with the image
            } else {
                // Image file doesn't exist
                bitmap=null;
                Log.e("bmb","Image file doesn't exist !");
            }
        } else {
            // Directory doesn't exist
            Log.e("bmb","Directory doesn't exist !");
            bitmap=null;
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
    public static  Bitmap getBitmapForAllVersions(Context context,File file){
        Bitmap bitmap=null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                //Uri uri = Uri.parse(imageFiles[jcntr].getAbsolutePath());
                Uri uri = AppUtil.getFileUri(context, file);
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Uri uri = AppUtil.getFileUri(context, file);
                bitmap = Utilities.getBitmapFromUri(context, uri);
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bitmap;
    }

    public static void copyImage(File sourceFile, File destFile) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
