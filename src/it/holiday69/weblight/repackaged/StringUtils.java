package it.holiday69.weblight.repackaged;

public class StringUtils
{
  public static String byteArrayToHexString(byte[] byteArray)
  {
    String hexString = "";
    for (int i = 0; i < byteArray.length; i++) {
      hexString = new StringBuilder().append(hexString).append(String.format("%02X%s", new Object[] { Byte.valueOf(byteArray[i]), "" })).toString();
    }
    return hexString;
  }

  public static String trimAll(String src)
  {
    String ret = src.trim();

    while (ret.endsWith("\n")) {
      ret = ret.substring(0, src.length() - 1);
    }
    while (ret.endsWith("\r")) {
      ret = ret.substring(0, src.length() - 1);
    }
    return ret;
  }

  public static <T> String join(String glue, T[] s)
  {
    int k = s.length;
    if (k == 0)
      return null;
    StringBuilder out = new StringBuilder();
    out.append(s[0]);
    for (int x = 1; x < k; x++)
      out.append(glue).append(s[x].toString());
    return out.toString();
  }

  public static boolean isEmpty(String val) {
    if (val == null) {
      return true;
    }
    if (val.trim().equals("")) {
      return true;
    }
    return false;
  }

  public static boolean hasContent(String val) {
    return !isEmpty(val);
  }

  public static String head(String src, String dividerRegex) {
    String[] splitList = src.split(dividerRegex);
    if (splitList.length == 0) {
      return src;
    }
    return splitList[0];
  }

  public static String tail(String src, String dividerRegex) {
    String[] splitList = src.split(dividerRegex);
    if (splitList.length == 0) {
      return src;
    }
    return splitList[(splitList.length - 1)];
  }

  public static String humanReadableByteCount(long bytes) {
    return humanReadableByteCount(bytes, false);
  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit) return new StringBuilder().append(bytes).append(" B").toString();
    int exp = (int)(Math.log(bytes) / Math.log(unit));
    String pre = new StringBuilder().append((si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)).append(si ? "" : "i").toString();
    return String.format("%.1f %sB", new Object[] { Double.valueOf(bytes / Math.pow(unit, exp)), pre });
  }

  public static String safeSubstring(String src, int maxCharCount) {
    if (isEmpty(src)) {
      return src;
    }
    if (src.length() <= maxCharCount) {
      return src;
    }
    return src.substring(0, maxCharCount);
  }
}