package gandalf.code;

import gandalf.model.GFile;
import jasonlib.Log;
import jasonlib.swing.component.GTextArea;
import jasonlib.util.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.swing.text.Document;
import com.google.common.base.Throwables;

public class CodeRunner {

  private static final Executor executor = Executors.newCachedThreadPool();

  private static final int MAX_BUFFER_SIZE = 99999;
  public static GTextArea console;

  private Process process;

  public CodeRunner run(GFile file, Runnable callback) {
    console.setText("");

    Log.debug("Running " + file);

    String name = file.name.substring(0, file.name.length() - 5);

    ProcessBuilder pb = new ProcessBuilder("java", name);
    pb.directory(QuickCompiler.SOURCE_DIR);

    pb.redirectErrorStream(true);

    executor.execute(() -> {
      handle(pb, callback);
    });

    return this;
  }

  public void stop() {
    process.destroyForcibly();
  }

  private void handle(ProcessBuilder pb, Runnable callback) {
    try {
      process = pb.start();

      Reader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      char[] buffer = new char[1024];
      long last = System.currentTimeMillis();
      long numBytes = 0;
      while (true) {
        int n = reader.read(buffer);
        if (n == -1) {
          break;
        }
        numBytes += n;

        String s = new String(buffer, 0, n);
        Document doc = console.getDocument();
        int overage = s.length() + doc.getLength() - MAX_BUFFER_SIZE;
        if (overage > 0) {
          try {
            doc.remove(0, overage + MAX_BUFFER_SIZE / 2);
          } catch (Exception e) {
            throw Throwables.propagate(e);
          }
        }
        console.append(s);
        console.scrollToBottom();

        long now = System.currentTimeMillis();
        if (now - last > 30) {
          if (numBytes > 9999) {
            Log.debug("Throttling output because numBytes was " + numBytes);
            Utils.sleep(30);
          }
          numBytes = 0;
          last = System.currentTimeMillis();
        }
      }
      callback.run();
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

}
