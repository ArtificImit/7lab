import client.Manager;
import server.Invoker;
import server.Server;
import commands.*;



public class ServerStarter {
    public static void main(String[] args){
        Invoker invoker = Invoker.getAccess();
        invoker.register("add", Add.class);
        invoker.register("min_by_creation_date", MinByCreationDate.class);
        invoker.register("remove_all_by_price", RemoveAllByPrice.class);
        invoker.register("filter_starts_with_name", FilterStartsWithName.class);
        invoker.register("clear", Clear.class);
        //invoker.register("save", Save.class);
        invoker.register("execute_script", ExecuteScript.class);
        invoker.register("exit", Exit.class);
        invoker.register("reorder", Reorder.class);
        invoker.register("help", Help.class);
        invoker.register("history", History.class);
        invoker.register("info", Info.class);
        invoker.register("sort", Sort.class);
        invoker.register("remove_by_id", RemoveById.class);
        invoker.register("show", Show.class);
        invoker.register("update", Update.class);
        invoker.register("register", Register.class);
        invoker.register("login", Login.class);
        Server server = new Server(8387);
        server.run();

        Manager manager = new Manager("localhost",  8387);
        manager.start();
    }
}
