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

    public static ActionAvailability getById(int id){
        switch (id){
            case 0:
                return PUBLISH;
            case 1:
                return SUBSCRIBE;
            case 2:
                return ALL;
            default:
                throw new IllegalArgumentException("Valid range for action availablity is 0-2");
        }
    }
}
