package commands;

import QA.Response;
import objectpack.Ticket;
import server.database.Collection;
import server.database.DatabaseManager;

public class Reorder extends Command implements Write {

    public <T extends Ticket> Reorder(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }

    @Override
    public Response execute() {
        return new Response(collection.sortInReverseOrder());
    }

    @Override
    public String getHelp() {
        return "Сортирует коллекцию в обратном порядке и выводит отсортированные элементы.";
    }
}
