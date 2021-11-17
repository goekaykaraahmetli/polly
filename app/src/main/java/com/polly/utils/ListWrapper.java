package com.polly.utils;

import java.util.List;

public class ListWrapper {
    private final List<Object> list;
    private final Class<?> type;

    public ListWrapper(List<Object> list, Class<?> type) {
        this.list = list;
        this.type = type;
    }

    public List<Object> getList(){
        return list;
    }

    public Class<?> getType(){
        return type;
    }
}
