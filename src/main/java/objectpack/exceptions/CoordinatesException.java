package objectpack.exceptions;
/**
 * Класс, расширяющий ArgumentVehicleException, обозначающий ошибку в работе с координатами
 * @see package objectpack.Coordinates
 */
public class CoordinatesException extends TicketArgumentException{
    public CoordinatesException(String msg, int argumentNumber){
        super(msg, argumentNumber);
    }
    public CoordinatesException(String msg, Throwable cause, int argumentNumber){
        super(msg, cause, argumentNumber);
    }
}
