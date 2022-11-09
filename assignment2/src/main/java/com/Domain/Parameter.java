package com.Domain;

public class Parameter {
    
    private String name;
    private String value;

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getParameterName() {
        return name;
    }

    public String getParameterValue() {
        return value;
    }

    public void setParameterName(String name) {
        this.name = name;
    }

    public void setParameterValue(String value) {
        this.value = value;
    }
}
