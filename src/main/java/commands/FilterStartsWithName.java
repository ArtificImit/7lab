package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

import java.util.stream.Collectors;

/**
 * Реализация команды filter_starts_with_name
 */
public class FilterStartsWithName extends Command implements Read{


    public <T extends Ticket> FilterStartsWithName(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);

    }

    /**
     * Метод, выводящий все элементы коллекции, имена которых начинаются с заданной строки pattern
     */
    @Override
    public Response execute() {
        return new Response(collection.searchByNameSubstring(argument));
    }

    @Override
    public String getHelp() {
        return "Выводит элементы, значение поля name которых начинается с заданной подстроки";
    }
}
