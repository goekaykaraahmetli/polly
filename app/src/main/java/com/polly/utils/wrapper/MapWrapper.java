package com.polly.utils.wrapper;

import java.util.Map;

public class MapWrapper {
    private final Map<Object, Object> map;
    private final Class<?> keyType;
    private final Class<?> valueType;

    public MapWrapper(Map<Object, Object> map, Class<?> keyType, Class<?> valueType) {
        this.map = map;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }
}
