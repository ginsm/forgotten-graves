package me.mgin.graves.util;

import java.sql.Array;
import java.util.ArrayList;

public class ArrayUtil {
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
