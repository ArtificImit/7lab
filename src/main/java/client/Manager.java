package client;

import QA.Request;
import QA.Response;
import commands.Authentication;
import commands.CommandMap;
import commands.CommandUsingElement;
import commands.CommandWithId;
import objectpack.*;
import objectpack.exceptions.TicketException;
import server.database.DatabaseManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Manager {
    private Scanner sc;
    private Client client;
    private CommandMap commandMap;
    private DatabaseManager dbManager; // Database manager for handling database operations
    private String username;

    public Manager(String server_address, int server_port, DatabaseManager dbManager) {
        this.sc = new Scanner(System.in);
        this.client = new Client(server_address, server_port);
        this.dbManager = dbManager; // Initialize DatabaseManager
    }
    public Manager(String server_address, int server_port){
        this.sc = new Scanner(System.in);
        this.client = new Client(server_address, server_port);
    }

    public void start() {
        Response response;
        response = this.client.start();
        this.commandMap = (CommandMap) response.getResponse()[0];

        System.out.println("Здравствуйте, пожалуйста используйте login или register для получения доступа");
        run();
    }

    public boolean checkIfNeedElement(String commandName) {
        return CommandUsingElement.class.isAssignableFrom(this.commandMap.get(commandName));
    }

    public boolean checkIfNeedId(String commandName) {
        return CommandWithId.class.isAssignableFrom(this.commandMap.get(commandName));
    }

    private String getArgumentWithRules(String msg, ArgumentChecker<String> checker) {
        String arg = "";
        System.out.println(msg);
        arg = this.sc.nextLine();
        while (!checker.check(arg)) {
            System.out.println("Неверный формат ввода. Попробуйте еще раз.");
            System.out.println(msg);
            arg = this.sc.nextLine();
        }
        return arg;
    }
    private ArrayList<String> readLogin(){
        ArrayList<String> args = new ArrayList<String>();
        args.add(getArgumentWithRules("Введите имя пользователя",
                arg -> !arg.trim().isEmpty()));
        args.add(getArgumentWithRules("Введите пароль",
                arg -> !arg.trim().isEmpty()));
        return args;
    }

    private ArrayList<String> readElement() {
        ArrayList<String> args = new ArrayList<>();

        args.add(getArgumentWithRules("Введите имя (непустая строка)", arg -> !arg.trim().isEmpty()));
        args.add(getArgumentWithRules("Введите первую координату в формате: x - целое число", arg -> ArgumentValidator.checkX(arg)));
        args.add(getArgumentWithRules("Введите вторую координату в формате: y - число с дробной частью", arg -> ArgumentValidator.checkY(arg)));
        args.add(getArgumentWithRules("Введите цену билета (целое положительное число, может быть пустым):", arg -> ArgumentValidator.checkPrice(arg)));
        args.add(getArgumentWithRules("Введите скидку на билет (целое число от 1 до 100):", arg -> ArgumentValidator.checkDiscount(arg)));
        args.add(getArgumentWithRules("Введите возможность возврата (true/false, может быть пустым):", arg -> ArgumentValidator.checkRefundable(arg)));

        List<TicketType> possibleTypes = Arrays.asList(TicketType.values());
        ArrayList<String> possibleTypesStr = new ArrayList<>();
        for (TicketType type : possibleTypes) {
            possibleTypesStr.add(type.toString());
        }
        args.add(getArgumentWithRules("Введите тип билета из представленных " + possibleTypesStr.toString() + ":", arg -> ArgumentValidator.checkType(arg, TicketType.class)));

        List<EventType> possibleEventTypes = Arrays.asList(EventType.values());
        ArrayList<String> possibleEventTypesStr = new ArrayList<>();
        for (EventType type : possibleEventTypes) {
            possibleEventTypesStr.add(type.toString());
        }
        args.add(getArgumentWithRules("Введите тип события из представленных " + possibleEventTypesStr.toString() + ":", arg -> ArgumentValidator.checkType(arg, EventType.class)));

        args.add(getArgumentWithRules("Введите имя события (непустая строка):", arg -> !arg.trim().isEmpty()));
        args.add(getArgumentWithRules("Введите дату проведения события (в формате 2024-09-03T12:00:00, можете и не вводить):", arg -> !arg.trim().isEmpty()));

        return args;
    }
    public boolean checkAuthentication(String commandName){
        return Authentication.class.isAssignableFrom(this.commandMap.get(commandName));
    }
    public void run() {
        String command = "";
        while (!command.equals("exit")) {
            ArrayList<String> element = new ArrayList<>();
            try {
                command = sc.nextLine();
                if (command.equals(""))
                    continue;

                String[] commandToCheck = command.split(" ");
                if (this.checkIfNeedId(commandToCheck[0])) {
                    if (!ArgumentValidator.checkId(commandToCheck))
                        continue;
                }
                if (this.checkIfNeedElement(commandToCheck[0]))
                    element = this.readElement();
                if (this.checkAuthentication(commandToCheck[0])){
                    if (!ArgumentValidator.checkAuth(commandToCheck)){
                        continue;
                    }
                }

                    Request request = new Request(command, element, true);
                    this.client.sendRequest(request);

            } catch (NoSuchElementException e) {
                sc.close();
                System.out.println("Программа завершена");
                Request request = new Request("exit", element, true);
                this.client.sendRequest(request);
            }

            System.out.println(this.client.receiveResponse());
        }
    }


    private void loadFromDatabase() throws SQLException {
        List<Ticket> tickets = dbManager.getAllTickets();
        // Display loaded tickets to the user
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }
}
