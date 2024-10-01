package server;
import objectpack.Ticket;
import commands.*;
import server.database.Collection;
import server.database.DatabaseManager;
import server.database.TicketStorageManager;
import server.database.UserDatabaseManager;
import server.util.InfoSender;
import server.util.OutStreamInfoSender;

import java.util.Deque;

public class Invoker {
    private static Invoker invoker;

    private CommandMap commandMap;

    private Invoker(){
        this.commandMap = new CommandMap();
    }

    public CommandMap getCommandMapClone(){
        return (CommandMap) commandMap.clone();
    }


    public static Invoker getAccess(){
        if(invoker == null)
            invoker = new Invoker();
        return invoker;
    }


    public void register(String name, Class<? extends Command> command){
        this.commandMap.put(name, command);

    }

    public <T extends Ticket> Command getCommandToExecute(String commandName, DatabaseManager collection, UserDatabaseManager userDatabaseManager, String argument, T el, Deque history, String userName) {
        Command instance = new BlankCommand(collection, argument, el, commandName, "null");
        if(this.commandMap.containsKey(commandName)) {
            InfoSender infoSender = new OutStreamInfoSender();
            try {
                Class<? extends  Command> command = this.commandMap.get(commandName);
                if(command.equals(History.class)){
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el, history, userName);
                } else if(command.equals(Help.class)){
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el, commandMap, userName);
                }
                else if(command.equals(Login.class) || command.equals(Register.class)){
                    instance = (Command) command.getConstructors()[0].newInstance(userDatabaseManager, argument, el);
                    infoSender.sendLine("Успешно создана команда " + instance);
                }
                else{
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el, userName);
                    infoSender.sendLine("Успешно создана команда " + instance);
                    infoSender.sendLine("Отправитель " + userName);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return instance;

    }
}
