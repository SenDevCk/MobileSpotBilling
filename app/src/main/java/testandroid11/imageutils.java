package testandroid11;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

public  class imageutils {

    public static Uri saveImage(Bitmap bitmap, Context context, String folderName) throws FileNotFoundException {
        Uri uri=null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
            }
        } else {
           File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"");
            // getExternalStorageDirectory is deprecated in API 29

            if (!dir.exists()) {
                dir.mkdirs();
            }

            java.util.Date date = new java.util.Date();
        File   imageFile = new File(dir.getAbsolutePath()
                    + File.separator
                    + new Timestamp(date.getTime()).toString()
                    + "Image.jpg");

            imageFile = new File(dir.getAbsolutePath()
                    + File.separator
                    + new Timestamp(date.getTime()).toString()
                    + "Image.jpg");
            saveImageToStream(bitmap, new FileOutputStream(imageFile));
            if (imageFile.getAbsolutePath() != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
                // .DATA is deprecated in API 29
                uri=null;
              uri=  context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        return uri;
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
