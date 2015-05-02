package gandalf.compile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import com.google.common.collect.Lists;

public class GCompiler extends Main {

  public List<CategorizedProblem> problems = Lists.newArrayList();

  public GCompiler(StringWriter out, StringWriter err){
    super(new PrintWriter(out), new PrintWriter(err), false, null, new SilentCompilationProgress());
  }

  @Override
  public boolean compile(String[] argv) {
    problems.clear();
    return super.compile(argv);
  }

  @Override
  public IProblemFactory getProblemFactory() {
    return new DefaultProblemFactory(this.compilerLocale) {
      @Override
      public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments,
          String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber) {
        return createProblem(originatingFileName, problemId, problemArguments, 0, messageArguments, severity,
            startPosition, endPosition, lineNumber, columnNumber);
      }

      @Override
      public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments,
          int elaborationId, String[] messageArguments, int severity, int startPosition, int endPosition,
          int lineNumber, int columnNumber) {
        CategorizedProblem ret = super.createProblem(originatingFileName, problemId, problemArguments, elaborationId,
            messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
        problems.add(ret);
        return ret;
      }

    };
  }

}
