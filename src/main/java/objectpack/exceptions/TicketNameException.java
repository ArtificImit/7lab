package objectpack.exceptions;

public class TicketNameException extends TicketArgumentException {
    public TicketNameException(String msg, int argumentNumber){
        super(msg, argumentNumber);
    }
    public TicketNameException(String msg, Throwable cause, int argumentNumber){
        super(msg, cause, argumentNumber);
    }
}

