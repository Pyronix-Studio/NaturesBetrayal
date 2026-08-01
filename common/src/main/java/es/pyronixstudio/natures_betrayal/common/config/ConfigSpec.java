package es.pyronixstudio.natures_betrayal.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigSpec {

    private final CommentedFileConfig config;
    private final List<Entry<?>> entries = new ArrayList<>();

    private ConfigSpec(Path path) {
        this.config = CommentedFileConfig.builder(path).autosave().build();
    }

    public void load() {
        if (Files.exists(config.getNioPath())) {
            config.load();
        }
        for (Entry<?> entry : entries) {
            entry.applyDefault(config);
        }
        config.save();
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
            this.comments = comments;
            spec.entries.add(this);
        }

        void applyDefault(CommentedFileConfig config) {
            if (!config.contains(key)) {
                Object toml = (defaultValue instanceof Enum<?> e) ? e.name() : defaultValue;
                config.set(key, toml);
            }
            if (!comments.isEmpty()) {
                config.setComment(key, String.join("\n", comments));
            }
        }

        @SuppressWarnings("unchecked")
        public T get() {
            return (T) spec.config.getOrElse(key, defaultValue);
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
            if (!comments.isEmpty()) {
                config.setComment(key, String.join("\n", comments));
            }
        }

        @Override
        public E get() {
            String raw = spec.config.getOrElse(key, defaultValue.name());
            try {
                return Enum.valueOf(enumClass, raw.toUpperCase());
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        }
    }

    public static class Builder {
        private final ConfigSpec spec;
        private List<String> comments = new ArrayList<>();

        public Builder(Path path) {
            this.spec = new ConfigSpec(path);
        }

        public Builder comment(String comment) {
            comments.add(comment);
            return this;
        }

        public <T> Entry<T> define(String key, T defaultValue) {
            List<String> c = consumeComments();
            Entry<T> entry = new Entry<>(spec, key, defaultValue, c);
            spec.entries.add(entry);
            return entry;
        }

        @SuppressWarnings("unchecked")
        public <E extends Enum<E>> EnumEntry<E> defineEnum(String key, E defaultValue, E... allowed) {
            List<String> c = consumeComments();
            c.add("Allowed Values: " + Arrays.stream(allowed)
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
            EnumEntry<E> entry = new EnumEntry<>(spec, key, defaultValue,
                    (Class<E>) defaultValue.getClass(), c);
            spec.entries.add(entry);
            return entry;
        }

        public ConfigSpec build() {
            return spec;
        }

        private List<String> consumeComments() {
            List<String> c = new ArrayList<>(comments);
            comments = new ArrayList<>();
            return c;
        }
    }
}