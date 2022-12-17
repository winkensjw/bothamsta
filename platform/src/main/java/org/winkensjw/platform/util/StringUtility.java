package org.winkensjw.platform.util;

@SuppressWarnings("unused")
public class StringUtility {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean hasText(String s) {
        int len;
        if (s == null || (len = s.length()) == 0) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String removeLinebreaks(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
    }

    public static String removeSuffixes(String s, String... suffixes) {
        for (int i = suffixes.length - 1; i >= 0; i--) {
            if (suffixes[i] != null && s.toLowerCase().endsWith(suffixes[i].toLowerCase())) {
                s = s.substring(0, s.length() - suffixes[i].length());
            }
        }
        return s;
    }

    public static String join(String delimiter, Object... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        boolean added = false;
        StringBuilder builder = new StringBuilder();
        for (Object o : parts) {
            if (o == null) {
                continue;
            }
            String s = o.toString();
            if (!isNullOrEmpty(s)) {
                if (added && delimiter != null) {
                    builder.append(delimiter);
                }
                builder.append(s);
                added = true;
            }
        }
        return builder.toString();
    }

}
