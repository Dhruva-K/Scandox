package com.example.docscanx.Retrofit.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.net.URISyntaxException;

public class Common {
//code for return uri(file path that we click from our camera)
    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException
    {
        String selection = null;
        String[] selectionArgs = null;

        if("content".equalsIgnoreCase(uri.getScheme()))
        {
            String[] projection ={
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try
            {
                cursor = context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if(cursor.moveToFirst())
                {
                    return cursor.getString(column_index);
                }

            }
            catch (Exception e){
                
            }
        }
        return null;
    }
}
