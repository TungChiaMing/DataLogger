package uk.ac.sussex.wear.android.datalogger;
import android.content.Context;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ModelDownloader {
  private final OkHttpClient client = new OkHttpClient();
  private final Context context;

  public ModelDownloader(Context ctx) {
    this.context = ctx.getApplicationContext();
  }

  public void downloadCheckpoint(String url, final String localFilename) {
    Request req = new Request.Builder()
        .url(url)
        .build();

    client.newCall(req).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        // 下载失败
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
          // 服务器返回错误
          throw new IOException("Unexpected code " + response);
        }

        // 把响应体写入内部存储
        InputStream is = response.body().byteStream();
        FileOutputStream fos = context.openFileOutput(localFilename, Context.MODE_PRIVATE);

        byte[] buf = new byte[8192];
        int len;
        while ((len = is.read(buf)) != -1) {
          fos.write(buf, 0, len);
        }
        fos.close();
        is.close();

        // 下载并保存完成，文件路径 = context.getFilesDir() + "/" + localFilename
      }
    });
  }
}