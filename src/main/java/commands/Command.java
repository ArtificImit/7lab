package commands;
import lombok.AllArgsConstructor;
import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.TicketStorageManager;

import java.sql.SQLException;

/**
 *
 * Абстрактный класс команды, который реализуют все команды

 */
@AllArgsConstructor
public abstract class Command implements CommandInt{

    protected DatabaseManager collection;
    protected String argument;
    protected Ticket el;
    protected String userName;

    public abstract Response execute();
    public abstract String getHelp();


    @Override
    public String toString() {
        return "Command{" +
                "collection=" + collection +
                ", argument='" + argument + '\'' +
                ", el=" + el +
                '}';
    }
}
