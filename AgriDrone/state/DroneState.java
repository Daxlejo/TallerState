package AgriDrone.state;

import AgriDrone.context.DroneContext;

public interface DroneState {

    void takeOff(DroneContext context);

    void startMapping(DroneContext context);

    void takePhoto(DroneContext context);

    void returnToBase(DroneContext context);

    void land(DroneContext context);

    void motorFailure(DroneContext context);

    void reset(DroneContext context);

    default void enter(DroneContext context) {}

    String getStateName();
}
