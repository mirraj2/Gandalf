package gandalf.code;

import org.eclipse.jdt.core.compiler.CompilationProgress;

public class SilentCompilationProgress extends CompilationProgress {

  @Override
  public void begin(int remainingWork) {
  }

  @Override
  public void done() {
  }

  @Override
  public boolean isCanceled() {
    return false;
  }

  @Override
  public void setTaskName(String name) {
  }

  @Override
  public void worked(int workIncrement, int remainingWork) {
  }

}
