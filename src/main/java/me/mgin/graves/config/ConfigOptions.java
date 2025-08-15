package me.mgin.graves.config;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigOptions {
    public record OptionMetaData(
            String categoryName,
            Class<?> categoryType,
            Class<?> valueType,
            MethodHandle getCategory,
            MethodHandle getValue
    ) {}

    public static Map<String, OptionMetaData> META_DATA = new HashMap<>();
    public static Map<String, ArrayList<String>> CATEGORY_OPTIONS = new HashMap<>();

    public static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

            for (Field category : GravesConfig.class.getDeclaredFields()) {
                String categoryName = category.getName();
                Class<?> categoryType = category.getType();
                ArrayList<String> categoryOptions = new ArrayList<>();

                for (Field option : categoryType.getDeclaredFields()) {
                    String optionName = option.getName();
                    Class<?> optionType = option.getType();
                    MethodHandle getCategory = LOOKUP.unreflectGetter(category);
                    MethodHandle getValue = LOOKUP.unreflectGetter(option);

                    OptionMetaData metaData = new OptionMetaData(
                            categoryName, categoryType, optionType, getCategory, getValue
                    );
                    META_DATA.put(optionName, metaData);

                    categoryOptions.add(optionName);
                }

                CATEGORY_OPTIONS.put(categoryName, categoryOptions);
            }

            CATEGORY_OPTIONS = Collections.unmodifiableMap(CATEGORY_OPTIONS);
            META_DATA = Collections.unmodifiableMap(META_DATA);

            initialized = true;
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException("Error initializing the Config option's meta data", e);
        }
    }

    public static <T> T getOptionValue(GravesConfig config, String option) {
        OptionMetaData metaData = META_DATA.get(option);
        if (metaData == null) throw new IllegalArgumentException("Unknown option: " + option);
        try {
            Object category = metaData.getCategory.invoke(config);
            Object value = metaData.getValue.invoke(category);

            return (T) value;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get value for option: " + option);
        }
    }
}
