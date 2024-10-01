package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.UserPermissionException;

import java.util.NoSuchElementException;

/**
 *
 * Реализация команды update
 */
public class Update extends Command implements CommandWithId, Write{

    public <T extends Ticket> Update(DatabaseManager storage, String argument, T el, String userName) {
        super(storage, argument, el, userName);
    }

    /**
     * Метод, обновляющий элемент в коллекции по его id и выводящий результат
     */
    @Override
    public Response execute() {
        try {
            if(collection.update(Integer.parseInt(argument), userName)){
                return new Response("Обновлён");
            }
            else{
                return new Response("Такого элемента нет");
            }
        }
        catch (UserPermissionException e){
            Response response = new Response("Недостаточно прав");
            return response;

        }
    }

    @Override
    public String getHelp() {
        return "Обновляет значение элемента коллекции, id которого равен заданному";
    }
}
