package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

/**
 *
 * Реализация команды exit
 */
public class Exit extends Command {
    public <T extends Ticket> Exit(DatabaseManager storage, String argument, T el, String userName) {
        super(storage, argument, el, userName);
    }

    /**
     * Метод, завершающий работу программы без сохранения коллекции
     */
    @Override
    public Response execute() {
        return new Response("До свидания!");
    }

    @Override
    public String getHelp() {
        return "Завершает программу (без сохранения в файл)";
    }
}
