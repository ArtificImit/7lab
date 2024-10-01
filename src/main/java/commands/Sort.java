package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Реализация команды sort
 */
public class Sort extends Command implements Write{

    public <T extends Ticket> Sort(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }

    /**
     * Метод, сортирующий коллекцию по цене и возвращающий отсортированную коллекцию
     */
    @Override
    public Response execute() {
        return new Response(collection.sortTicketsByName());
    }

    @Override
    public String getHelp() {
        return "Сортирует коллекцию по цене и выводит отсортированные элементы.";
    }
}
