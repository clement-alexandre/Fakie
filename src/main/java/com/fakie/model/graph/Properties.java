package com.fakie.model.graph;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Properties extends Iterable<Property> {
    boolean hasProperty(String key);

    Object getProperty(String key);

    void setProperty(String key, Object value);

    default void setProperties(Properties properties) {
        for (Property property : properties) {
            setProperty(property.getKey(), property.getValue());
        }
    }

    Set<String> keys();

    Collection<Object> values();

    List<Object> values(String key);

    Type type(String key);

    default Stream<Property> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
