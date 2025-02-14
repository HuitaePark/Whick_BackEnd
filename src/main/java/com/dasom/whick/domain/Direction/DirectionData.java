package com.dasom.whick.domain.Direction;

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

}