package AgriDrone.model;

public class Drone {

    private final Battery battery;
    private final Camera camera;
    private boolean motorsActive;

    public Drone() {
        this.battery = new Battery();
        this.camera = new Camera();
        this.motorsActive = false;
    }

    public Battery getBattery() {
        return battery;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean areMotorsActive() {
        return motorsActive;
    }

    public void activateMotors() {
        this.motorsActive = true;
    }

    public void deactivateMotors() {
        this.motorsActive = false;
    }
}
