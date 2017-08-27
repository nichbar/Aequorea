package nich.work.aequorea.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtils {
    private static final String mPicDir = "/Pictures/Screenshots/";
    
    public static String saveBitmapToExternalStorage(Bitmap bitmap, String name) {
        
        File newDir = new File(Environment.getExternalStorageDirectory().getPath() + mPicDir);
        if (!newDir.isDirectory()) {
            newDir.mkdir();
        }
        
        String fileName = name + ".jpg";
        File file = new File(newDir, fileName);
        
        if (file.exists()) file.delete();
        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return file.getPath();
    }
    
    public static Bitmap loadBitmapFromStorage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }
}
