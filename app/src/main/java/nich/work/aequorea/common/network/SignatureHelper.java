package nich.work.aequorea.common.network;

import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

public class SignatureHelper {
    public static String jaMi(LinkedHashMap<String, Object> linkedHashMap) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("64925f300924b0OzZRpXXz5CqIficrntbhjxmb");
            if (linkedHashMap != null && linkedHashMap.size() != 0) {
                for (Object obj : linkedHashMap.values()) {
                    formatValues(sb, obj);
                }
            }
            sb.append("CBNWeeklyAPI");
            String sb2 = sb.toString();
            String md5 = md5(sb2);
            return Base64.encodeToString(md5.getBytes(), Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }
    
    private static void formatValues(StringBuilder sb, Object obj) {
        if (obj != null && !obj.toString().equals("")) {
            int i = 0;
            String obj3 = obj.toString();
            if (obj3.startsWith("{")) {
                for (Object obj4 : new JsonObject().getAsJsonArray(obj3)) {
                    formatValues(sb, obj4);
                }
            } else if (obj3.startsWith("[")) {
                JsonArray parseArray = new JsonObject().getAsJsonArray(obj3);
                int size2 = parseArray.size();
                while (i < size2) {
                    formatValues(sb, parseArray.get(i));
                    i++;
                }
            } else {
                sb.append(obj);
            }
        }
    }
    
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
