package com.dasom.whick.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollisionNotification {
    private String type;
    private CollisionData data;

    @Getter
    @Setter
    public static class CollisionData {
        private boolean risk;
    }
}