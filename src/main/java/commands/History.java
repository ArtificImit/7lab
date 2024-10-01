package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;
import server.util.Pair;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
/**
 *
 * Реализация команды history
 */
public class History extends Command implements Read{
    /**
     * История команд в виде пар (Имя, Объект класса команды)
     */
    private Deque<Pair<String, Command>> history;

    public <T extends Ticket> History(DatabaseManager storage, String argument, T el, Deque<Pair<String, Command>> history, String username) {
        super(storage, argument, el, username);
        this.history = history;
    }

    private <T extends Ticket> History(DatabaseManager storage, String argument, T el, String userName){
        super(storage, argument, el, userName);
    }
    /**
     * Метод, выводящий последние 7 команд
     */
    @Override
    public Response execute() {
        return new Response(history.stream().map(Pair::getFirst).collect(Collectors.toCollection(LinkedList::new)).toArray());
    }

    @Override
    public String getHelp() {
        return "Выводит последние 7 команд (без их аргументов)";
    }
}
