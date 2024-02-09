package org.cso.MSBUtil;

import android.content.Context;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by 41008931 on 09/10/2017.
 */

public class ImageFileFilter implements FileFilter
{
    Context ctx = null;
    UtilDB utilDB = null;


    public boolean accept(File file)
    {
        String strMRU = utilDB.getActiveMRU();
        if(strMRU.equalsIgnoreCase("") || strMRU == null)
            return true;
        if (file.getName().contains(strMRU))
        {
            return true;
        }
        return false;
    }

    public ImageFileFilter(Context ctx) {
        this.ctx = ctx;
        utilDB = new UtilDB(ctx);

    }
}