package AgriDrone.exception;

public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String currentState, String action) {
        super("No se puede realizar '" + action + "' en el estado '" + currentState + "'");
    }
}
