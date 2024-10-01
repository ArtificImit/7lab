package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.UserPermissionException;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * Реализация команды remove_all_by_price
 */
public class RemoveAllByPrice extends Command implements Write{
    private Integer price; // Цена, по которой будет происходить удаление

    public <T extends Ticket> RemoveAllByPrice(DatabaseManager collection, String argument, T el, String userName) {
        super(collection, argument, null, userName);
        try {
            this.price = Integer.parseInt(argument);
        }
        catch (NumberFormatException e){
            this.price = null;
        }

    }

    /**
     * Метод, удаляющий все элементы с указанной ценой и возвращающий результат операции
     */
    @Override
    public Response execute() {
        try {
            try {
                int res = collection.removeAllByPrice(Integer.parseInt(argument), userName);
                return new Response("Удалено "+ res + " элементов");
            } catch (UserPermissionException e) {
                return new Response("Изменения затрагивают элемент другого пользователя");
            }
        } catch (SQLException e) {
            return new Response("Ошибка в процессе удаления ", e.getStackTrace());
        }
    }

    @Override
    public String getHelp() {
        return "Удаляет все элементы из коллекции с указанной ценой";
    }
}
