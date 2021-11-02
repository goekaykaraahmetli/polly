package com.polly.interfaces.communication;

import java.util.ArrayList;
import java.util.List;

class WrappedData {
    private final Class dataType;
    private final Object data;
    private final List<Class> generics;

    public WrappedData(Class dataType, Object data){
        this.dataType = dataType;
        this.data = data;
        this.generics = new ArrayList<>();
    }

    public WrappedData(Class dataType, Object data, List<Class> generics){
        this.dataType = dataType;
        this.data = data;
        this.generics = generics;
    }

    public Object getData(){
        return data;
    }

    public Class getDataType(){
        return dataType;
    }

    public List<Class> getGenerics(){
        return generics;
    }
}
