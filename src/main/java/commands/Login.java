package commands;
import QA.Response;
import commands.Authentication;
import commands.Command;
import objectpack.Ticket;
import server.database.UserDatabaseManager;

/**
 * Команда для входа пользователя в систему.
 */
public class Login extends Command implements Authentication {

    private UserDatabaseManager userDb;

    private String username;
    private String password;
    private String[] args;

    public <T extends Ticket> Login(UserDatabaseManager userDb, String argument, T el) {
        super(null, argument, null, null); // не требует коллекции и элемента
        args = argument.split(" ");
        this.userDb = userDb;
    }

    @Override
    public Response execute() {
       if (args.length==2) {
           this.username = args[0];
           this.password = args[1];

        if (userDb.checkUserExisted(username)) {
            if (userDb.checkPassword(username, password)){
                return new Response("Добро пожаловать ", username);
            }
            else {
                return new Response("Неправильный пароль");
            }
        } else {
            return new Response("Пользователь не найден");
        }
       }
       else
           return new Response("Неправильный формат команды");
    }

    @Override
    public String getHelp() {
        return "Использование: login";
    }
}