package objectpack.exceptions;

/**
 * Абстрактный класс ошибки при работе аргументами объекта типа Ticket
 * @see objectpack.Ticket
 */
public abstract class TicketArgumentException extends Exception{

    /**
     * Поле равное порядковому номеру аргумента в вводе, то есть каким по счету вводится аргумент
     */
    public final int argumentNumber;

    public TicketArgumentException(String msg, int argumentNumber){
        super(msg);
        this.argumentNumber = argumentNumber;
    }
    public TicketArgumentException(String msg, Throwable cause, int argumentNumber){
        super(msg, cause);
        this.argumentNumber = argumentNumber;
    }

}
