package gandalf.code;

import jasonlib.Log;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ClassIndex {

  private static final List<String> blacklist = ImmutableList.of("javax.xml", "java.beans");

  private final Multimap<String, String> nameClasses = ArrayListMultimap.create();

  public ClassIndex() {
    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        Stopwatch watch = Stopwatch.createStarted();
        init();
        Log.debug("Indexed JDK classes in " + watch);
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }
    });
  }

  public Collection<String> getClasses(String token) {
    return nameClasses.get(token);
  }

  private void init() throws Exception {
    for (String c : getBuiltInClasses()) {
      int j = c.lastIndexOf('.');
      int i = c.lastIndexOf('.', j - 1);
      nameClasses.put(c.substring(i + 1, j), c.substring(0, j));
    }
  }

  private List<String> getBuiltInClasses() throws Exception {
    List<String> ret = Lists.newArrayList();

    URL url = Object.class.getResource("String.class");

    String s = url.toString();
    s = s.substring(0, s.lastIndexOf("!"));
    if (s.startsWith("jar:")) {
      s = s.substring(4);
    }

    url = new URL(s);

    ZipInputStream zip = new ZipInputStream(url.openStream());
    for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
      if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
        String name = entry.getName();
        if (name.startsWith("java")) {
          name = name.replace('/', '.');
          if (accept(name)) {
            ret.add(name);
          }
        }
      }
    }

    return ret;
  }

  private boolean accept(String className) {
    for (String s : blacklist) {
      if (className.startsWith(s)) {
        return false;
      }
    }

    return true;
  }

  public static void main(String[] args) throws Exception {
    new ClassIndex();
  }

}
