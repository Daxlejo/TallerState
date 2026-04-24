package AgriDrone.state;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;
import AgriDrone.model.Battery;
import AgriDrone.model.Camera;

/**
 * Mapping State (Mapeo).
 * The drone is actively taking topographic photos.
 * Each photo drains battery. If battery falls to 15% or below,
 * the drone automatically transitions to BatteryReturnState.
 * Motor failure can occur at any time.
 */
public class MappingState implements DroneState {

    @Override
    public void takeOff(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Despegar");
    }

    @Override
    public void startMapping(DroneContext context) {
        context.getLogger().log("\u2139\uFE0F El dron ya est\u00e1 en modo de mapeo.");
    }

    @Override
    public void takePhoto(DroneContext context) {
        Battery battery = context.getDrone().getBattery();
        Camera camera = context.getDrone().getCamera();

        // Take the photo
        int photoCount = camera.capturePhoto();
        battery.drain();
        context.getLogger().log("\uD83D\uDCF8 Foto topogr\u00e1fica #" + photoCount
                + " capturada. Bater\u00eda: " + battery.getLevel() + "%");

        // Check battery level — automatic transition
        if (battery.isLow()) {
            context.getLogger().log("\uD83D\uDD0B \u00A1Bater\u00eda baja! Nivel: " + battery.getLevel()
                    + "%. Iniciando retorno autom\u00e1tico a base...");
            camera.deactivate();
            context.getLogger().log("\uD83D\uDCF7 C\u00e1mara desactivada para ahorrar energ\u00eda.");
            context.setState(new BatteryReturnState());
            context.getLogger().log("\uD83D\uDD04 Retornando a base por bater\u00eda baja.");
        }
    }

    @Override
    public void returnToBase(DroneContext context) {
        context.getDrone().getCamera().deactivate();
        context.setState(new ManualReturnState());
    }

    @Override
    public void land(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Aterrizar");
    }

    @Override
    public void motorFailure(DroneContext context) {
        context.getLogger().log("\u26A0\uFE0F \u00A1ALERTA CR\u00CDTICA! Fallo de motor durante el mapeo.");
        context.getDrone().getCamera().deactivate();
        context.getDrone().deactivateMotors();
        context.getLogger().log("\uD83D\uDCF7 C\u00e1mara y motores desactivados.");
        context.setState(new EmergencyLandingState());
        context.getLogger().log("\uD83D\uDEA8 Procedimiento de aterrizaje de emergencia activado.");
    }

    @Override
    public void reset(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Resetear");
    }

    @Override
    public String getStateName() {
        return "Mapeo";
    }
}
