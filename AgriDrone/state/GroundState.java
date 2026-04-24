package AgriDrone.state;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;

/**
 * Ground State (EnTierra).
 * The drone is on the ground. Only takeOff is allowed.
 */
public class GroundState implements DroneState {

    @Override
    public void takeOff(DroneContext context) {
        context.getDrone().activateMotors();
        context.getDrone().getBattery().startDischarge();
        context.getLogger().log("\u2705 Motores activados. Descarga de batería iniciada.");
        context.setState(new TakeoffState());
        context.getLogger().log("\uD83D\uDE80 El dron ha despegado exitosamente.");
    }

    @Override
    public void startMapping(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Iniciar Mapeo");
    }

    @Override
    public void takePhoto(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Tomar Foto");
    }

    @Override
    public void returnToBase(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Retornar a Base");
    }

    @Override
    public void land(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Aterrizar");
    }

    @Override
    public void motorFailure(DroneContext context) {
        context.getLogger().log("\u2139\uFE0F El dron ya est\u00e1 en tierra. No hay riesgo de fallo de motor.");
    }

    @Override
    public void reset(DroneContext context) {
        context.getLogger().log("\u2139\uFE0F El dron ya est\u00e1 en estado inicial.");
    }

    @Override
    public String getStateName() {
        return "En Tierra";
    }
}
