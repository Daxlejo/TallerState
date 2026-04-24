package AgriDrone.context;

import AgriDrone.model.Drone;
import AgriDrone.state.DroneState;
import AgriDrone.state.GroundState;
import AgriDrone.util.DroneLogger;

public class DroneContext {

    private DroneState currentState;
    private final Drone drone;
    private final DroneLogger logger;

    public DroneContext() {
        this.drone = new Drone();
        this.logger = new DroneLogger();
        this.currentState = new GroundState();
        this.logger.log("\uD83D\uDEE9\uFE0F Dron inicializado. Estado: En Tierra.");
    }

    public synchronized void takeOff() {
        currentState.takeOff(this);
    }

    public synchronized void startMapping() {
        currentState.startMapping(this);
    }

    public synchronized void takePhoto() {
        currentState.takePhoto(this);
    }

    public synchronized void returnToBase() {
        currentState.returnToBase(this);
    }

    public synchronized void land() {
        currentState.land(this);
    }

    public synchronized void motorFailure() {
        currentState.motorFailure(this);
    }

    public synchronized void reset() {
        currentState.reset(this);
    }

    public void setState(DroneState newState) {
        this.currentState = newState;
        this.currentState.enter(this);
    }

    public DroneState getCurrentState() {
        return currentState;
    }

    public Drone getDrone() {
        return drone;
    }

    public DroneLogger getLogger() {
        return logger;
    }

    public synchronized String getStatusJson() {

        checkBattery();

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"state\":\"").append(escapeJson(currentState.getStateName())).append("\",");
        sb.append("\"battery\":").append(drone.getBattery().getLevel()).append(",");
        sb.append("\"batteryLow\":").append(drone.getBattery().isLow()).append(",");
        sb.append("\"cameraActive\":").append(drone.getCamera().isActive()).append(",");
        sb.append("\"photosTaken\":").append(drone.getCamera().getPhotosTaken()).append(",");
        sb.append("\"motorsActive\":").append(drone.areMotorsActive()).append(",");

        boolean isReturning = currentState instanceof AgriDrone.state.BatteryReturnState
                || currentState instanceof AgriDrone.state.ManualReturnState;

        sb.append("\"actions\":{");
        sb.append("\"takeOff\":").append(!isReturning && currentState instanceof GroundState).append(",");
        sb.append("\"startMapping\":").append(!isReturning && currentState instanceof AgriDrone.state.TakeoffState)
                .append(",");
        sb.append("\"takePhoto\":").append(!isReturning && currentState instanceof AgriDrone.state.MappingState)
                .append(",");
        sb.append("\"returnToBase\":").append(!isReturning && (currentState instanceof AgriDrone.state.TakeoffState
                || currentState instanceof AgriDrone.state.MappingState)).append(",");
        sb.append("\"land\":").append(false).append(",");
        sb.append("\"motorFailure\":").append(!isReturning && isInAir()).append(",");
        sb.append("\"reset\":").append(!isReturning && currentState instanceof AgriDrone.state.EmergencyLandingState);
        sb.append("},");

        sb.append("\"log\":[");
        var entries = logger.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            sb.append("\"").append(escapeJson(entries.get(i))).append("\"");
            if (i < entries.size() - 1)
                sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private void checkBattery() {
        if (drone.getBattery().isLow() && (currentState instanceof AgriDrone.state.MappingState
                || currentState instanceof AgriDrone.state.TakeoffState)) {
            logger.log("\uD83D\uDD0B ¡Batería baja! Nivel: " + drone.getBattery().getLevel()
                    + "%. Retorno automático a base.");
            drone.getCamera().deactivate();
            setState(new AgriDrone.state.BatteryReturnState());
        }
    }

    private boolean isInAir() {
        return currentState instanceof AgriDrone.state.TakeoffState
                || currentState instanceof AgriDrone.state.MappingState
                || currentState instanceof AgriDrone.state.BatteryReturnState;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
