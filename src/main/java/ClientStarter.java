import client.Manager;
import server.Invoker;
import server.Server;
import commands.*;
import server.database.DatabaseManager;

public class ClientStarter {
    public static void main(String[] args){
        Manager manager = new Manager("localhost",  8387);
        manager.start();
    }
}
