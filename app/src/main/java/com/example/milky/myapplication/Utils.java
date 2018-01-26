package com.example.milky.myapplication;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Klasa do obsługi jednego ze sposobów pobierania
 * obrazu z linku, która nie zadzialała ze względu
 * na wykonywanie operacji wewnątrz głównego wątku
 */

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
