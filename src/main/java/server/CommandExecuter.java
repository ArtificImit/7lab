package server;

import QA.AuthRequest;
import QA.Request;
import QA.Response;
import lombok.Getter;
import objectpack.Ticket;
import server.database.*;
import server.filework.*;
import server.util.InfoSender;
import server.util.OutStreamInfoSender;
import server.util.Pair;
import commands.Command;
import commands.ExecuteScript;
//import commands.Save;
import commands.BlankCommand;

import java.sql.SQLException;
import java.util.*;

/**
 * Класс - исполнитель комманд
 */
public class CommandExecuter {

    private static CommandExecuter server;

    @Getter
    private boolean authPass = false;
    private DatabaseManager storage;
    private UserDatabaseManager userDb;


    /** Список команд, элементы которого пары вида (Название команды, Объект класса команды)
     * @see Pair
     */
    private LinkedList<Pair<String, Command>> history;
    private Invoker invoker;
    /** @see Invoker */
    private FileReader fileReader;
    /** @see FileReader */
    private InfoSender infoSender;
    /** @see InfoSender */
    private FileSaver fileSaver;
    /** @see FileSaver */
    private LinkedList<String> executedRecursionScript = new LinkedList<>();
    /**
     * Статический метод, предоставляющий доступ к экземпляру класса исполнителя комманд
     */
    public static CommandExecuter getAccess(){
        if(server == null)
            server = new CommandExecuter();
        return server;
    }

    /** Конструктор класса, задающий все параметры и загрудающий коллекцию из файла
     * @see OutStreamInfoSender
     * @see FileInputStreamReader
     * @see CSVFileSaver
     * @see CSVLoader
     */
    private CommandExecuter()  {
        this.invoker = Invoker.getAccess();
        this.history = new LinkedList<>();
        this.infoSender = new OutStreamInfoSender();
        this.fileReader = new FileInputStreamReader();
        this.fileSaver = new CSVFileSaver();
        try {
            // Устанавливаем соединение с базой данных
            this.storage = DatabaseManager.getAccess("jdbc:postgresql://localhost:5432/studs", "s412968", "kZe6BnQLBzwcR2iE", "Tickets");
            this.userDb = UserDatabaseManager.getAccess("jdbc:postgresql://localhost:5432/studs", "s412968", "kZe6BnQLBzwcR2iE", "Users");



        } catch (SQLException e) {
            this.infoSender.sendLine("Ошибка при работе с базой данных "+"\n" +e.getMessage() +"\n" + Arrays.toString(e.getStackTrace()));
        }


    }

    public void addExecutedRecursionScript(String arg) {
        executedRecursionScript.add(arg);
    }
    public void clearExecutedRecursionScript() {
        executedRecursionScript.clear();
    }
    public void clearUsers(){
        userDb.clear();
    }
    public Response executeAuthCommand(AuthRequest authRequest){
        String userName = authRequest.userName;
        String command_name = authRequest.command_name;
        String argument = authRequest.argument;
        Ticket ticket = authRequest.element;
        Response response;
        if ("login".equals(command_name) || "register".equals(command_name)) {
            return new Response("Вы уже вошли, чтобы выйти из аккаунта пропишите exit");
        }
        Command commandToExecute = invoker.getCommandToExecute(command_name, storage, userDb, argument, ticket, history, userName);
        if (authRequest.sentFromClient) {
            if (commandToExecute instanceof ExecuteScript) {
                this.executedRecursionScript.add(argument);
            } else if (authRequest.saveFlag) {
                response = new Response("Сохранение происходит автоматически");
                authRequest.saveFlag = false;
                return response;
            }
        } else {
            System.out.println("Выполняется сохранение");
        }
        response = commandToExecute.execute();
        if (!(commandToExecute instanceof BlankCommand))
            this.writeCommandToHistory(new Pair<>(command_name, commandToExecute));
        for (String s : executedRecursionScript) {
            if (s.equals(argument)) {
                response = new Response("Рекурсия в скрипте! Инструкция пропущена. Скрипт продолжается...");
                return response;
            }
        }
        return response;
    }

    public void setAuthPass(boolean authPass) {
        this.authPass = authPass;
    }

    /**
     * Метод, который выбирает команду по ее названию, исполняет ее и записывает в историю команд.
     * Также здесь происходит парсинг объекта типа Vehicle из строк
     * @param request запрос
     */
    public Response executeLoginCommand(Request request/*ArrayList<String> element*/) {

        String command_name = request.command_name;
        String argument = request.argument;
        Ticket ticket = request.element;
        Response response;
        if ("login".equals(command_name) || "register".equals(command_name)) {
            infoSender.sendLine("Выполняется вход или регистрация");
            Command commandToExecute = invoker.getCommandToExecute(command_name, storage, userDb, argument, ticket, history, "");
            response = commandToExecute.execute();
            infoSender.sendLine("Получен ответ " + response);
            if(!(commandToExecute instanceof BlankCommand))
                this.writeCommandToHistory(new Pair<>(command_name, commandToExecute));
            if (!response.equals(new Response("Неправильный пароль")) && !response.equals(new Response("Пользователь не найден")) && !response.equals(new Response("Имя пользователя занято")) && !response.equals(new Response("Неправильный формат команды"))) {
                authPass = true;
                infoSender.sendLine("Установлен флаг выполнения входа");
            }
            return response;

        }
        else{
            return new Response("Пожалуйста войдите или зарегистрируйтесь перед тем как продолжать");
        }

    }

    public boolean checkExecutedRecursionScript(String toCheck) {
        if (executedRecursionScript.contains(toCheck)){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Метод добавляет команду в историю и если история содержит более 7 элементов удаляет первый.
     * @param command Команда в виде пары (Имя Команды, Объект Команды)
     */
    private void writeCommandToHistory(Pair<String, Command> command){
        if(this.history.size() == 7)
            this.history.removeFirst();
        this.history.add(command);
    }
    public void externalSave(){
        // (new Save(this.collection, "", null)).execute();
    }

    public void setExecutedRecursionScript(LinkedList<String> executedRecursionScript) {
        this.executedRecursionScript = executedRecursionScript;
    }
}