package AgriDrone.model;

public class Camera {

    private boolean active;
    private int photosTaken;

    public Camera() {
        this.active = false;
        this.photosTaken = 0;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public int capturePhoto() {
        this.photosTaken++;
        return this.photosTaken;
    }

    public boolean isActive() {
        return active;
    }

    public int getPhotosTaken() {
        return photosTaken;
    }
}
