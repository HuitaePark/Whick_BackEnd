package com.dasom.whick.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectionData {
    private boolean left;
    private boolean right;

    public DirectionData() {
    }

    public DirectionData(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    // Getters and Setters

    @Override
    public String toString() {
        return "DirectionData{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}