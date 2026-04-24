package AgriDrone.state;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;

/**
 * Takeoff State (Despegue).
 * The drone has taken off and is ready to start the mapping mission.
 * Allows: startMapping, motorFailure.
 */
public class TakeoffState implements DroneState {

    @Override
    public void takeOff(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Despegar");
    }

    @Override
    public void startMapping(DroneContext context) {
        context.getDrone().getCamera().activate();
        context.getLogger().log("\uD83D\uDCF7 C\u00e1mara activada. Iniciando misi\u00f3n de mapeo...");
        context.setState(new MappingState());
        context.getLogger().log("\uD83D\uDDFA\uFE0F Modo de mapeo activo. Listo para tomar fotos topogr\u00e1ficas.");
    }

    @Override
    public void takePhoto(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Tomar Foto");
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
        context.getLogger().log("\u26A0\uFE0F \u00A1ALERTA! Fallo de motor detectado durante el despegue.");
        context.getDrone().deactivateMotors();
        context.setState(new EmergencyLandingState());
        context.getLogger().log("\uD83D\uDEA8 Procedimiento de aterrizaje de emergencia activado.");
    }

    @Override
    public void reset(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Resetear");
    }

    @Override
    public String getStateName() {
        return "Despegue";
    }
}
