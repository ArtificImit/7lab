package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Реализация команды info
 */
public class Info extends Command implements Read{

    public <T extends Ticket> Info(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }

    /**
     * Метод, выводящий типа, дату создания и количество элементов коллекци
     */
    @Override
    public Response execute() {

        return new Response(collection.getDatabaseMetadata());
    }

    @Override
    public String getHelp() {
        return "Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
