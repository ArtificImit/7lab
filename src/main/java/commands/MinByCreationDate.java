package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

import java.util.Comparator;
import java.util.Optional;

/**
 * Реализация команды min_by_creation_date
 */
public class MinByCreationDate extends Command implements Read {
    /**
     * @see Collection
     */
    private DatabaseManager collection;

    public <T extends Ticket> MinByCreationDate(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
        this.collection = collection;
    }

    /**
     * Метод, находящий элемент коллекции с минимальной датой создания
     */
    @Override
    public Response execute() {

        return new Response(collection.getOldestTicket());

    }

    @Override
    public String getHelp() {
        return "Находит элемент с минимальной датой создания";
    }
}
