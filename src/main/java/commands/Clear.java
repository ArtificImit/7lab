package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

/**
 * Реализация команды clear
 */
public class Clear extends Command implements Write{


    public <T extends Ticket> Clear(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }

    /**
     * Метод, очищающий коллекцию
     */
    @Override
    public Response execute() {
        this.collection.clear();
        return new Response("Коллекция очищена");
    }

    @Override
    public String getHelp() {
        return "Очищает коллекцию";
    }
}
