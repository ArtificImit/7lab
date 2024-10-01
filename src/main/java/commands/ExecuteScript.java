package commands;

import objectpack.Ticket;
import objectpack.exceptions.TicketArgumentException;
import server.CommandExecuter;
import QA.Request;
import QA.Response;
import server.database.DatabaseManager;
import server.filework.FileInputStreamReader;
import server.filework.FileReader;
import server.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
/**
 *
 * Реализация команды execute_script
 */
public class ExecuteScript extends Command{
    /**
     * @see CommandExecuter
     */
    private CommandExecuter commandExecuter;
    /**
     * @see FileReader
     */
    private FileReader fileReader;


    public <T extends Ticket> ExecuteScript(DatabaseManager collection, String argument, T el, String userName){
        super(collection, argument, el, userName);
        this.commandExecuter = CommandExecuter.getAccess();
        this.fileReader = new FileInputStreamReader();
    }
    /**
     * Метод, считывающий команды с файла и проверяющий их на валидность, затем передающий их в исполнитель команд для исполнения
     */
    @Override
    public Response execute() {
       ArrayList<String> element = new ArrayList<>();
       element.add("");
        Request saveRequest = new Request("save", element, true);
        LinkedList<Pair<String, ArrayList<String>>> commandList;
        try {
            commandList = fileReader.readCommandsFromFile(argument);
            this.commandExecuter.addExecutedRecursionScript(argument);
        } catch (FileNotFoundException | NullPointerException e) {

            return new Response("Файл не найден \n" + "Полученный путь: " + argument + "\n" + Arrays.toString(e.getStackTrace()));


        } catch (SecurityException e){
            return new Response("Не хватает прав для доступа к файлу");
        } catch (IOException e){
            return new Response(e.getStackTrace());
        }


        ListIterator<Pair<String, ArrayList<String>>> it = commandList.listIterator();

        LinkedList<String> response = new LinkedList<>();
        try {
            boolean flag = false;
            while (it.hasNext()) {
                Pair<String, ArrayList<String>> command = it.next();

                ArrayList<String> scriptPath = new ArrayList<>();
                Pair<String, ArrayList<String>> script = new Pair<>("execute_script", scriptPath);
                scriptPath.add(argument);
                System.out.println("argument: "+argument);
                script.setFirst("execute_script");
//                boolean ans = command == script;
//                response.add(Boolean.toString(ans));
//                response.add("Command: [" + command + "] Length: " + command.getFirst().length() + " " + command.getSecond().size());
//                response.add("Script: [" + script + "] Length: " + script.getFirst().length() + " " + script.getSecond().size());
                if(script.equals(command)) {
                    flag = true;
                    response.add("Скрипт вызывает сам себя. Команда вызова скрипта в скрипте была пропущена");
                    command.setFirst("");
                    ArrayList<String> ass = new ArrayList<>();
                    ass.add("");
                    command.setSecond(ass);
                }
                String[] scriptTest;
                scriptTest = command.getFirst().split(" ");
                if (scriptTest[0].equals("execute_script")){
                    String correctPath;
                    // Убираем квадратные скобки в начале и в конце строки
                    if (command.getSecond().toString().startsWith("[") && command.getSecond().toString().endsWith("]")) {
                        correctPath =  command.getSecond().toString().substring(1, command.getSecond().toString().length() - 1);
                    } else if (command.getSecond().toString().startsWith("[")) {
                        correctPath = command.getSecond().toString().substring(1); // Убираем только открывающую скобку
                    } else if (command.getSecond().toString().endsWith("]")) {
                        correctPath = command.getSecond().toString().substring(0, command.getSecond().toString().length() - 1); // Убираем только закрывающую скобку
                    }
                    else {
                        correctPath = command.getSecond().toString();
                    }
                    if(!commandExecuter.checkExecutedRecursionScript(correctPath)) {
                        this.commandExecuter.addExecutedRecursionScript(correctPath);
                    }
                    else {
                        response.add("Скрипт был выполнен не полностью вследствие наличия в нём рекурсии, пожалуйста исправьте проблему");
                        this.commandExecuter.clearExecutedRecursionScript();
                        return new Response(response.toArray());
                    }
                    if (scriptTest.length > 1) {
                        this.commandExecuter.addExecutedRecursionScript(scriptTest[1]);
                    }
                }
                if (!flag) {
                    command.setFirst(command.getFirst() + " " + command.getSecond());
                    Request request = new Request(command.getFirst(), command.getSecond(), false);

                    //response.add("Аргумент " + command.getSecond().toString());
                    response.add("Выполняется команда: " + request.command_name);
                    response.add(this.commandExecuter.executeLoginCommand(request).toString());
                }

                //this.commandExecuter.executeCommand(command.getFirst(), command.getSecond());
            }

            this.commandExecuter.executeLoginCommand(saveRequest);
            response.add("Скрипт Выполнен");
            this.commandExecuter.clearExecutedRecursionScript();
            return new Response(response.toArray());
        }

        catch (NumberFormatException e){
            int commandError = it.previousIndex();
            response.add("Аргумент команды на " + (commandError + 1) + " строке должен быть целым числом меньшим 2^32");

        }
        catch (Exception e){
            int commandError = it.previousIndex();
            response.add("Скрипт Выполнен до "+ (commandError + 1) + " строки:");
            response.add("Ошибка в команде на " + (commandError + 1) + " строке: " + e.getMessage());
            Throwable cause = e.getCause();
            if(cause instanceof TicketArgumentException){
                int errorLine = commandError + ((TicketArgumentException) cause).argumentNumber + 1;
                response.add("Строка " + errorLine + ": " + cause.getMessage());
            }
            else if(cause instanceof IllegalArgumentException){
                response.add("Требуется 5 аргументов соответствующих требованиям");
            }
            else{
                response.add("Непредвиденная ошибка");
                e.printStackTrace();
                response.add(Arrays.toString(e.getStackTrace()));
                response.add(e.getMessage());
            }
            commandExecuter.setExecutedRecursionScript(new LinkedList<>());
        }

        return new Response(response.toArray());
    }

    @Override
    public String getHelp() {
        return "Считывает и исполняет скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }
}
