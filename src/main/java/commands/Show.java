package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

import java.sql.SQLException;
import java.util.Arrays;

/**
 *
 * Реализация команды show
 */
public class Show extends Command implements Read{

    public <T extends Ticket> Show(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }


    /**
     * Метод, выводящий все элементы коллекции в порядке их добавления
     */
    @Override
    public Response execute() {
        return new Response(collection.getStorage());


    }

    @Override
    public String getHelp() {
        return "Выводит  в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
