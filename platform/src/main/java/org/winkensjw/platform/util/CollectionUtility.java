package org.winkensjw.platform.util;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unused")
public class CollectionUtility {

    public static boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean hasElements(Map<?, ?> m) {
        return !isEmpty(m);
    }

    public static boolean hasElements(Collection<?> c) {
        return !isEmpty(c);
    }

    public static int size(Map<?, ?> m) {
        return isEmpty(m) ? 0 : m.size();
    }

    public static int size(Collection<?> c) {
        return isEmpty(c) ? 0 : c.size();
    }
}
