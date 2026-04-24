package AgriDrone.state;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;

public class EmergencyLandingState implements DroneState {

    @Override
    public void takeOff(DroneContext context) {
        throw new InvalidStateTransitionException(getStateName(), "Despegar");
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
        context.getLogger().log("\u2139\uFE0F El dron ya est\u00e1 en aterrizaje de emergencia.");
    }

    @Override
    public void reset(DroneContext context) {
        context.getDrone().deactivateMotors();
        context.getDrone().getCamera().deactivate();
        context.getDrone().getBattery().stopDischarge();
        context.getDrone().getBattery().recharge();
        context.getLogger().log("\uD83D\uDD27 Sistemas reseteados. Bater\u00eda recargada.");
        context.getLogger().log("\u2705 Inspecci\u00f3n de motores completada. Dron operativo.");
        context.setState(new GroundState());
        context.getLogger().log("\uD83C\uDFE0 Dron listo en tierra para una nueva misi\u00f3n.");
    }

    @Override
    public String getStateName() {
        return "Aterrizaje de Emergencia";
    }
}
