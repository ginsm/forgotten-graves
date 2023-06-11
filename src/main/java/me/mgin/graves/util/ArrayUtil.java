package me.mgin.graves.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil {
    /**
     * Combines lists of the same type to create a new list.
     *
     * @param lists {@code List<T>... lists}
     * @return {@code List<T>}
     */
    @SafeVarargs
    static public <T> List<T> merge(List<T>... lists) {
        List<T> result = new ArrayList<>();

        for (List<T> list : lists) {
            result.addAll(list);
        }

        return result;
    }

    static public <T> int indexOf(T[] array, T target) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
