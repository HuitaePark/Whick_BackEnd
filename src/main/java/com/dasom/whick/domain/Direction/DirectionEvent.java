package com.dasom.whick.domain.Direction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DirectionEvent {
    private String type;
    private DirectionData data;

    public DirectionEvent(String type, DirectionData data) {
        this.type = type;
        this.data = data;
    }

}