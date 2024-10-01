package commands;

import QA.Response;
import objectpack.Ticket;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.TicketStorageManager;

/**
 *
 * Реализация команды add
 */
public class Add extends Command implements CommandUsingElement, Write{

    public <T extends Ticket> Add(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName);
    }

    /**
     * Метод, добавляющий элемент в коллекцию и выводящий результат (добавлен или не добавлен)
     */
    @Override
    public Response execute() {

        if(this.collection.addTicket(el, userName))
            return new Response("Элемент добавлен");
        else {
            return new Response("Элемент не был добавлен");
        }
    }

    @Override
    public String getHelp() {
        return "добавляет новый элемент в коллекцию, ввод элемента осущестлявется в следующих 5 строках";
    }
}
