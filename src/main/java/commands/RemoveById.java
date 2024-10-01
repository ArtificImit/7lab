package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.UserPermissionException;

import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 *
 * Реализация команды remove_by_id
 */
public class RemoveById extends Command implements CommandWithId, Write{
    /**
     * id элемента, который будет удален
     */

    public <T extends Ticket> RemoveById(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, el, userName );
    }


    /**
     * Метод, удаляющий элемент по его id и выводящий результат операци
     */
    @Override
    public Response execute() {
        try {
            if(collection.removeById(Long.parseLong(argument), userName)) {
                return new Response("Элемент удалён");
            }
            else {
                return new Response("Элемент с таким id не найден");
            }
        } catch (UserPermissionException e) {
            return new Response("Вы не можете удалить элемент созданный другим пользователем");
        }
    }

    @Override
    public String getHelp() {
        return "Удаляет элемент из коллекции по его id";
    }
}
