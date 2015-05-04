package gandalf.code;

import static jasonlib.util.Utils.sleep;
import gandalf.model.GFile;
import gandalf.model.Project;
import gandalf.ui.editor.CodeEditor;
import jasonlib.IO;
import jasonlib.Log;
import jasonlib.OS;
import java.io.File;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class QuickCompiler {

  public static final File SOURCE_DIR = new File(OS.getAppFolder("gandalf"), "src");
  static {
    SOURCE_DIR.mkdirs();
  }

  private CodeEditor editor;
  private Project project;

  private long lastEdit = 0, lastCompile = -1;

  public QuickCompiler(CodeEditor editor, Project project) {
    this.editor = editor;
    this.project = project;

    editor.change(this::onEdit);

    Executors.newSingleThreadExecutor().execute(this::compiler);
  }

  private void compile() {
    Log.info("Compiling...");

    StringBuilder sb = new StringBuilder("-8 -classpath rt.jar");

    for (File child : SOURCE_DIR.listFiles()) {
      child.delete();
    }

    for (GFile file : project.files) {
      File diskFile = new File(SOURCE_DIR, file.name);
      sb.append(" ").append(diskFile.getPath());
      IO.from(file.getContent()).to(diskFile);
    }

    StringWriter output = new StringWriter();
    StringWriter error = new StringWriter();
    GCompiler compiler = new GCompiler(output, error);

    compiler.compile(Main.tokenize(sb.toString()));
    Multimap<String, CategorizedProblem> multimap = Multimaps.index(compiler.problems,
        problem -> new String(problem.getOriginatingFileName()));

    for (GFile file : project.files) {
      file.setProblems(ImmutableList.of());
    }
    for (String key : multimap.keySet()) {
      GFile file = project.getFile(new File(key).getName());
      file.setProblems(multimap.get(key));
    }

    editor.onCompileFinished();
  }

  private void compiler() {
    while (true) {
      if (lastCompile < lastEdit) {
        long now = System.currentTimeMillis();
        if (now - lastEdit > 500) {
          lastCompile = now;
          compile();
        }
      }
      sleep(30);
    }
  }

  private void onEdit() {
    lastEdit = System.currentTimeMillis();
  }

}
