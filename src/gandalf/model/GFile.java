package gandalf.model;

import static com.google.common.collect.Iterables.concat;
import java.util.Collection;
import java.util.List;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import com.google.common.collect.Lists;

public class GFile {

  public String name;
  private String content;

  public final List<CategorizedProblem> errors = Lists.newArrayList(), warnings = Lists.newArrayList();

  private List<Runnable> listeners = Lists.newArrayList();

  public GFile(String name, String content) {
    this.name = name;
    this.content = content;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;

    for (Runnable listener : listeners) {
      listener.run();
    }
  }

  public void setProblems(Collection<CategorizedProblem> problems) {
    errors.clear();
    warnings.clear();
    for (CategorizedProblem problem : problems) {
      if (problem.isError()) {
        errors.add(problem);
      } else if (problem.isWarning()) {
        warnings.add(problem);
      } else {
        throw new IllegalStateException("Not error or warning: " + problem);
      }
    }
  }

  public CategorizedProblem getProblemAt(int index) {
    for (CategorizedProblem problem : concat(errors, warnings)) {
      if (problem.getSourceStart() <= index && problem.getSourceEnd() >= index - 1) {
        return problem;
      }
    }
    return null;
  }

  public void change(Runnable callback) {
    listeners.add(callback);
  }

}
