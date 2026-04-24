package AgriDrone.state;

import AgriDrone.context.DroneContext;
import AgriDrone.exception.InvalidStateTransitionException;

public class BatteryReturnState implements DroneState {

    @Override
    public void enter(DroneContext context) {
        int returnTimeSeconds = java.util.concurrent.ThreadLocalRandom.current().nextInt(5, 13);
        long delayMillis = returnTimeSeconds * 1000L;

        String logMsg = String.format("\uD83D\uDD04 Regresando automaticamente a la base (tiempo estimado %d segundos)",
                returnTimeSeconds);
        context.getLogger().log(logMsg);

        new java.util.Timer(true).schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (context.getCurrentState() instanceof BatteryReturnState) {
                    context.land();
                }
            }
        }, delayMillis);
    }

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
        context.getLogger().log("\u274C No se pueden tomar fotos. C\u00e1mara desactivada para ahorrar bater\u00eda.");
    }

    @Override
    public void returnToBase(DroneContext context) {
        context.getLogger().log("\u2139\uFE0F El dron ya est\u00e1 retornando a base.");
    }

    @Override
    public void land(DroneContext context) {
        context.getDrone().deactivateMotors();
        context.getDrone().getBattery().stopDischarge();
        context.getDrone().getBattery().recharge();
        context.getLogger().log("\uD83D\uDEEC Aterrizaje exitoso. Motores apagados.");
        context.getLogger().log("\uD83D\uDD0B Bater\u00eda recargada al 100%.");
        context.setState(new GroundState());
        context.getLogger().log("\u2705 Dron en tierra. Listo para una nueva misi\u00f3n.");
    }

    @Override
    public void motorFailure(DroneContext context) {
        context.getLogger().log("\u26A0\uFE0F \u00A1ALERTA CR\u00CDTICA! Fallo de motor durante el retorno.");
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
        return "Retorno por Bater\u00eda";
    }
}
