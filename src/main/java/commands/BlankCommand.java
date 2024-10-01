package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;

/**
 *
 * Реализация "неизветсной" команды, то есть той, которой нет в списке команд
 */
public class BlankCommand extends Command {

    /**
     * Имя несуществующей команды
     */
    private String command;

    public <T extends Ticket> BlankCommand(DatabaseManager collection, String argument, T el, String command, String userName) {
        super(collection, argument, el, userName);
        this.command = command;
    }

    /**
     * Метод, выводящий информацию о несуществовании команды
     */
    @Override
    public Response execute() {
        return new Response("команды \"" + this.command + "\" нет, чтобы вывести список комманд используйте help");
    }

    @Override
    public String getHelp() {
        return null;
    }
}
