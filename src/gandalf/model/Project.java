package gandalf.model;

import jasonlib.IO;
import java.util.List;
import com.google.common.collect.Lists;

public class Project {

  public static final String DEFAULT_NAME = "New Project";

  public String name = DEFAULT_NAME;
  public final List<GFile> files = Lists.newArrayList();

  public Project() {
    files.add(new GFile("Main.java", IO.from(getClass(), "main").toString()));
    files.add(new GFile("Temp.java", IO.from(getClass(), "main").toString().replace("Main", "Temp")));
  }

  public GFile getFile(String name) {
    for (GFile file : files) {
      if (file.name.equals(name)) {
        return file;
      }
    }
    return null;
  }

}
