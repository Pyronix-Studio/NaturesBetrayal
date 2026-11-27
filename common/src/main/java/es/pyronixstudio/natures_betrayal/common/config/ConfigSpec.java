package es.pyronixstudio.natures_betrayal.common.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.IndentStyle;
import com.electronwill.nightconfig.toml.TomlWriter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigSpec {

    private final CommentedFileConfig config;
    private final List<Entry<?>> entries = new ArrayList<>();
    private final List<String> sections = new ArrayList<>();
    private final Map<String, List<String>> sectionComments = new HashMap<>();

    private ConfigSpec(Path path) {
        this.config = CommentedFileConfig.builder(path).preserveInsertionOrder().build();
    }

    public void load() {
        if (Files.exists(config.getNioPath())) {
            config.load();
        }
        for (Entry<?> entry : entries) {
            entry.applyDefault(config);
        }
        for (String sectionPath : sections) {
            if (!config.contains(sectionPath)) {
                config.set(sectionPath, config.createSubConfig());
            }
        }
        reorder(config, List.of());
        for (Entry<?> entry : entries) {
            entry.applyComments(config);
        }
        for (Map.Entry<String, List<String>> section : sectionComments.entrySet()) {
            List<String> comments = section.getValue();
            if (!comments.isEmpty()) {
                config.setComment(section.getKey(), String.join("\n", comments));
            }
        }
        saveWithoutIndent();
    }

    private void saveWithoutIndent() {
        TomlWriter writer = new TomlWriter();
        writer.setIndent(IndentStyle.NONE);
        try (Writer output = Files.newBufferedWriter(config.getNioPath())) {
            writer.write(config, output);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save config: " + config.getNioPath(), e);
        }
    }

    private void registerSection(String sectionPath, List<String> comments) {
        if (!sections.contains(sectionPath)) {
            sections.add(sectionPath);
        }
        if (!comments.isEmpty()) {
            sectionComments.put(sectionPath, comments);
        }
    }

    private void reorder(CommentedConfig target, List<String> parentPath) {
        if (target.isEmpty()) return;

        Map<String, Object> snapshot = new LinkedHashMap<>();
        for (Config.Entry entry : target.entrySet()) {
            snapshot.put(entry.getKey(), entry.getRawValue());
        }

        List<String> orderedKeys = new ArrayList<>();

        for (Entry<?> entry : entries) {
            if (isDirectChild(entry.key, parentPath)) {
                addUnique(orderedKeys, lastSegment(entry.key));
            }
        }

        for (String sectionPath : sections) {
            if (parentPathOf(sectionPath).equals(parentPath)) {
                addUnique(orderedKeys, lastSegment(sectionPath));
            }
        }

        for (String key : snapshot.keySet()) {
            addUnique(orderedKeys, key);
        }

        for (String key : snapshot.keySet()) {
            target.remove(key);
        }
        for (String key : orderedKeys) {
            if (snapshot.containsKey(key)) {
                target.set(key, snapshot.get(key));
            }
        }

        for (Map.Entry<String, Object> value : snapshot.entrySet()) {
            if (value.getValue() instanceof CommentedConfig child) {
                List<String> childPath = new ArrayList<>(parentPath);
                childPath.add(value.getKey());
                reorder(child, childPath);
            }
        }
    }

    private static boolean isDirectChild(String key, List<String> parentPath) {
        String[] parts = key.split("\\.");
        if (parts.length != parentPath.size() + 1) return false;
        for (int i = 0; i < parentPath.size(); i++) {
            if (!parentPath.get(i).equals(parts[i])) return false;
        }
        return true;
    }

    private static List<String> parentPathOf(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot < 0) return List.of();
        return Arrays.asList(path.substring(0, lastDot).split("\\."));
    }

    private static String lastSegment(String path) {
        int lastDot = path.lastIndexOf('.');
        return lastDot < 0 ? path : path.substring(lastDot + 1);
    }

    private static void addUnique(List<String> list, String value) {
        if (!list.contains(value)) {
            list.add(value);
        }
    }

    private static List<String> appendDefaultComment(List<String> comments, Object defaultValue) {
        List<String> result = new ArrayList<>(comments);
        Object value = defaultValue instanceof Enum<?> e ? e.name() : defaultValue;
        result.add("Default: " + value);
        return result;
    }

    private static List<String> appendRangeComment(List<String> comments, Object min, Object max) {
        List<String> result = new ArrayList<>(comments);
        result.add("Allowed Range: " + min + " - " + max);
        return result;
    }

    // --- Entry ---

    public static class Entry<T> {
        protected final String key;
        protected final T defaultValue;
        protected final List<String> comments;
        protected final ConfigSpec spec;

        private Entry(ConfigSpec spec, String key, T defaultValue, List<String> comments) {
            this.spec = spec;
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = appendDefaultComment(comments, defaultValue);
            spec.entries.add(this);
        }

        void applyDefault(CommentedFileConfig config) {
            if (!config.contains(key)) {
                config.set(key, toConfigValue(defaultValue));
                return;
            }
            T current = fromConfigValue(config.get(key));
            T valid = normalize(current);
            if (!Objects.equals(current, valid)) {
                config.set(key, toConfigValue(valid));
            }
        }

        void applyComments(CommentedFileConfig config) {
            if (!comments.isEmpty()) {
                config.setComment(key, String.join("\n", comments));
            }
        }

        public T get() {
            return normalize(fromConfigValue(spec.config.getOrElse(key, defaultValue)));
        }

        protected T normalize(T value) {
            return value;
        }

        protected Object toConfigValue(T value) {
            return value instanceof Enum<?> e ? e.name() : value;
        }

        @SuppressWarnings("unchecked")
        protected T fromConfigValue(Object raw) {
            return (T) raw;
        }
    }

    public abstract static class NumberEntry<T extends Number> extends Entry<T> {
        protected final T min;
        protected final T max;

        private NumberEntry(ConfigSpec spec, String key, T defaultValue, T min, T max, List<String> comments) {
            super(spec, key, defaultValue, comments);
            this.min = min;
            this.max = max;
        }

        protected abstract T cast(Number value);

        protected abstract T clamp(T value);

        @Override
        protected T normalize(T value) {
            return value == null ? defaultValue : clamp(value);
        }

        @Override
        protected T fromConfigValue(Object raw) {
            return raw instanceof Number number ? cast(number) : null;
        }
    }

    public static final class IntEntry extends NumberEntry<Integer> {
        private IntEntry(ConfigSpec spec, String key, int defaultValue, int min, int max, List<String> comments) {
            super(spec, key, defaultValue, min, max, comments);
        }

        @Override
        protected Integer cast(Number value) {
            return value.intValue();
        }

        @Override
        protected Integer clamp(Integer value) {
            return Math.clamp(value, min, max);
        }
    }

    public static final class LongEntry extends NumberEntry<Long> {
        private LongEntry(ConfigSpec spec, String key, long defaultValue, long min, long max, List<String> comments) {
            super(spec, key, defaultValue, min, max, comments);
        }

        @Override
        protected Long cast(Number value) {
            return value.longValue();
        }

        @Override
        protected Long clamp(Long value) {
            return Math.clamp(value, min, max);
        }
    }

    public static final class FloatEntry extends NumberEntry<Float> {
        private FloatEntry(ConfigSpec spec, String key, float defaultValue, float min, float max, List<String> comments) {
            super(spec, key, defaultValue, min, max, comments);
        }

        @Override
        protected Float cast(Number value) {
            return value.floatValue();
        }

        @Override
        protected Float clamp(Float value) {
            if (Float.isNaN(value)) return min;
            return Math.clamp(value, min, max);
        }
    }

    public static final class DoubleEntry extends NumberEntry<Double> {
        private DoubleEntry(ConfigSpec spec, String key, double defaultValue, double min, double max, List<String> comments) {
            super(spec, key, defaultValue, min, max, comments);
        }

        @Override
        protected Double cast(Number value) {
            return value.doubleValue();
        }

        @Override
        protected Double clamp(Double value) {
            if (Double.isNaN(value)) return min;
            return Math.clamp(value, min, max);
        }
    }

    public static class EnumEntry<E extends Enum<E>> extends Entry<E> {
        private final Class<E> enumClass;

        private EnumEntry(ConfigSpec spec, String key, E defaultValue, Class<E> enumClass, List<String> comments) {
            super(spec, key, defaultValue, comments);
            this.enumClass = enumClass;
        }

        @Override
        void applyDefault(CommentedFileConfig config) {
            if (!config.contains(key)) {
                config.set(key, defaultValue.name());
            }
        }

        @Override
        public E get() {
            Object raw = spec.config.getOrElse(key, defaultValue.name());
            if (raw instanceof String value) {
                try {
                    return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return defaultValue;
        }
    }

    public static class Builder {
        private final ConfigSpec spec;
        private List<String> comments = new ArrayList<>();
        private final Deque<String> path = new ArrayDeque<>();

        public Builder(Path path) {
            this.spec = new ConfigSpec(path);
        }

        public Builder comment(String comment) {
            comments.add(comment);
            return this;
        }

        public Section push(String section) {
            path.addLast(section);
            String fullPath = String.join(".", path);
            spec.registerSection(fullPath, consumeComments());
            return new Section(this);
        }

        public Builder pop() {
            if (!path.isEmpty()) {
                path.removeLast();
            }
            return this;
        }

        public static class Section {
            private final Builder builder;

            private Section(Builder builder) {
                this.builder = builder;
            }

            public Section comment(String comment) {
                builder.comment(comment);
                return this;
            }

            public <T> Entry<T> define(String key, T defaultValue) {
                return builder.define(key, defaultValue);
            }

            public IntEntry define(String key, int defaultValue, int min, int max) {
                return builder.define(key, defaultValue, min, max);
            }

            public LongEntry define(String key, long defaultValue, long min, long max) {
                return builder.define(key, defaultValue, min, max);
            }

            public FloatEntry define(String key, float defaultValue, float min, float max) {
                return builder.define(key, defaultValue, min, max);
            }

            public DoubleEntry define(String key, double defaultValue, double min, double max) {
                return builder.define(key, defaultValue, min, max);
            }

            @SafeVarargs
            @SuppressWarnings("varargs")
            public final <E extends Enum<E>> EnumEntry<E> defineEnum(String key, E defaultValue, E... allowed) {
                return builder.defineEnum(key, defaultValue, allowed);
            }

            public Section push(String section) {
                return builder.push(section);
            }

            public Builder pop() {
                return builder.pop();
            }
        }

        public <T> Entry<T> define(String key, T defaultValue) {
            List<String> c = consumeComments();
            String fullKey = resolveKey(key);
            return new Entry<>(spec, fullKey, defaultValue, c);
        }

        public IntEntry define(String key, int defaultValue, int min, int max) {
            validateRange(key, min, max);
            List<String> c = appendRangeComment(consumeComments(), min, max);
            return new IntEntry(spec, resolveKey(key), clampInt(defaultValue, min, max), min, max, c);
        }

        public LongEntry define(String key, long defaultValue, long min, long max) {
            validateRange(key, min, max);
            List<String> c = appendRangeComment(consumeComments(), min, max);
            return new LongEntry(spec, resolveKey(key), clampLong(defaultValue, min, max), min, max, c);
        }

        public FloatEntry define(String key, float defaultValue, float min, float max) {
            validateRange(key, min, max);
            List<String> c = appendRangeComment(consumeComments(), min, max);
            return new FloatEntry(spec, resolveKey(key), clampFloat(defaultValue, min, max), min, max, c);
        }

        public DoubleEntry define(String key, double defaultValue, double min, double max) {
            validateRange(key, min, max);
            List<String> c = appendRangeComment(consumeComments(), min, max);
            return new DoubleEntry(spec, resolveKey(key), clampDouble(defaultValue, min, max), min, max, c);
        }

        @SafeVarargs
        @SuppressWarnings("varargs")
        public final <E extends Enum<E>> EnumEntry<E> defineEnum(String key, E defaultValue, E... allowed) {
            List<String> c = consumeComments();
            c.add("Allowed Values: " + Arrays.stream(allowed)
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
            String fullKey = resolveKey(key);
            return new EnumEntry<>(spec, fullKey, defaultValue,
                    defaultValue.getDeclaringClass(), c);
        }

        public ConfigSpec build() {
            return spec;
        }

        private String resolveKey(String key) {
            if (path.isEmpty()) return key;
            return String.join(".", path) + "." + key;
        }

        private List<String> consumeComments() {
            List<String> c = new ArrayList<>(comments);
            comments = new ArrayList<>();
            return c;
        }

        private static void validateRange(String key, long min, long max) {
            if (min > max) {
                throw new IllegalArgumentException("Invalid range for '" + key + "': min " + min + " > max " + max);
            }
        }

        private static void validateRange(String key, double min, double max) {
            if (Double.isNaN(min) || Double.isNaN(max) || Double.isInfinite(min) || Double.isInfinite(max)) {
                throw new IllegalArgumentException("Invalid range for '" + key + "': min/max must be finite");
            }
            if (min > max) {
                throw new IllegalArgumentException("Invalid range for '" + key + "': min " + min + " > max " + max);
            }
        }

        private static int clampInt(int value, int min, int max) {
            return Math.clamp(value, min, max);
        }

        private static long clampLong(long value, long min, long max) {
            return Math.clamp(value, min, max);
        }

        private static float clampFloat(float value, float min, float max) {
            if (Float.isNaN(value)) return min;
            return Math.clamp(value, min, max);
        }

        private static double clampDouble(double value, double min, double max) {
            if (Double.isNaN(value)) return min;
            return Math.clamp(value, min, max);
        }
    }
}
