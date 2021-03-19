package top.zibin.luban;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lym on 2021/3/19.
 */
public class UriInputStreamProvider extends InputStreamAdapter {

    private static final String TAG = "UriInputStreamProvider";

    private String path;
    private Context context;
    private Uri uri;
    private String cacheDir;

    public UriInputStreamProvider(Context context, Uri uri, String cacheDir) {
        this.context = context.getApplicationContext();
        this.uri = uri;
        this.cacheDir = cacheDir;
    }


    @Override
    public InputStream openInternal() throws IOException {
        return context.getContentResolver().openInputStream(uri);
    }


    @Override
    public String getPath() {
        if (path == null) {
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                path = uri.getPath();
            } else {
                try {
                    final File copy = new File(provideCacheDir(), "luban-" + System.currentTimeMillis() + ".jpg");
                    if (!copy.exists()) {
                        copy.createNewFile();
                    }
                    final FileOutputStream fos = new FileOutputStream(copy);
                    final InputStream in = open();
                    final byte[] buffer = new byte[1024 * 4];
                    try {
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "getPath Copy", e);
                    } finally {
                        fos.close();
                        in.close();
                    }
                    path = copy.getPath();
                } catch (IOException e) {
                    Log.e(TAG, "getPath", e);
                }
            }
        }
        return path;
    }

    private File provideCacheDir() {
        final File dir = new File(cacheDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }
}
