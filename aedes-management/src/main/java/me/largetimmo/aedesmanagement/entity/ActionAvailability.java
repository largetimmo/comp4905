package me.largetimmo.aedesmanagement.entity;

public enum ActionAvailability {
    PUBLISH(0),
    SUBSCRIBE(1),
    ALL(2);

    private Integer id;

    ActionAvailability(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
