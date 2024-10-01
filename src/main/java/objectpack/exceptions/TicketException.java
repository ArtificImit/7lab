package objectpack.exceptions;

/**
 * Класс, расширяющий RuntimeException, обозначающий ошибку в работе с Vehicle
 * @see objectpack.Ticket
 * @author Piromant
 */
public class TicketException extends Exception{
    public TicketException(String msg){
        super(msg);
    }
    public TicketException(String msg, Throwable cause){
        super(msg, cause);
    }
}
