package it.holiday69.weblight.repackaged;

public class ExceptionUtils
{
  public static String formatStackTrace(StackTraceElement[] stackTraceList)
  {
    String[] stackStringList = new String[stackTraceList.length];

    int cnt = 0;
    for (StackTraceElement elem : stackTraceList) {
      stackStringList[cnt] = new StringBuilder().append("[").append(cnt++).append("] ").append(elem.toString()).toString();
    }
    return join("\n", stackStringList);
  }

  private static String join(String glue, String[] s)
  {
    int k = s.length;
    if (k == 0)
      return null;
    StringBuilder out = new StringBuilder();
    out.append(s[0]);
    for (int x = 1; x < k; x++)
      out.append(glue).append(s[x]);
    return out.toString();
  }

  public static String getFullExceptionInfo(Throwable e)
  {
    String info = new StringBuilder().append(e.getClass().getSimpleName()).append(" : ").append(e.getMessage()).toString();

    if (e.getCause() != null) {
      return new StringBuilder().append(info).append(" CAUSED BY: ").append(e.getCause().getClass().getSimpleName()).append(" : ").append(e.getCause().getMessage()).append("\nStack trace:\n").append(formatStackTrace(e.getCause().getStackTrace())).toString();
    }
    return new StringBuilder().append(e.getClass().getSimpleName()).append(" : ").append(e.getMessage()).append("\nStack trace:\n").append(formatStackTrace(e.getStackTrace())).toString();
  }

  public static String getStackTraceAsString(Throwable e)
  {
    StackTraceElement[] list = e.getStackTrace();
    String[] stackStringList = new String[list.length];

    int cnt = 0;
    for (StackTraceElement elem : list) {
      stackStringList[cnt] = new StringBuilder().append("[").append(cnt++).append("] ").append(elem.toString()).toString();
    }
    return StringUtils.join("\n", stackStringList);
  }

  public static String getDisplableExceptionInfo(Throwable th)
  {
    if (th.getMessage() != null) {
      return th.getMessage();
    }
    if ((th.getCause() != null) && (th.getCause().getMessage() != null)) {
      return th.getCause().getMessage();
    }
    return th.getClass().getName();
  }
}