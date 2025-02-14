package com.dasom.whick.domain.Collision;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollisionNotificationDto {
    private String type;
    private CollisionData data;

    @Getter
    @Setter
    public static class CollisionData {
        private boolean risk;
    }
}