package com.dasom.whick.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectionEvent {
    private String type;
    private DirectionData data;

    public DirectionEvent() {
    }

    public DirectionEvent(String type, DirectionData data) {
        this.type = type;
        this.data = data;
    }

    // Getters and Setters

    @Override
    public String toString() {
        return "DirectionEvent{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}