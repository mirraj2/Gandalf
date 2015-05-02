package gandalf.model;

import java.util.Collection;
import java.util.List;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import com.google.common.collect.Lists;

public class GFile {

  public String name, content;

  public final List<CategorizedProblem> errors = Lists.newArrayList(), warnings = Lists.newArrayList();

  public GFile(String name, String content) {
    this.name = name;
    this.content = content;
  }

  @Override
  public String toString() {
    return name;
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

}
