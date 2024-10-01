//package commands;
//
//import objectpack.Ticket;
//import QA.Response;
//import server.database.Collection;
//import server.database.DatabaseManager;
//import server.filework.FileSaver;
//import server.filework.CSVFileSaver;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// *
// * Реализация команды save
// */
//public class Save extends Command{
//    /**
//     * @see FileSaver
//     */
//    private final FileSaver fileSaver;
//    /**
//     * Имя файла, в который будет сохранена коллекция
//     */
//    //private final String fileName;
//
//
//    public <T extends Ticket> Save(DatabaseManager collection, String argument, T el, String userName) {
//        super(collection, argument, el, userName);
//        this.argument = System.getenv("SAVEFILE");
//        this.fileSaver = new CSVFileSaver();
//    }
//
//    /**
//     * Метод, что сохраняет коллекцию в файл, и сообщает если сохранение не удается
//     */
//    @Override
//    public Response execute() {
//        List<String> response = new LinkedList<>();
//        try {
//
//            response.add("Файл сохранен");
//        } catch (FileNotFoundException e) {
//            File file = new File(this.argument);
//            if(!file.exists()) {
//                response.add("Переданный аргумент для пути файла: " + argument);
//                response.add("Файл не найден");
//            }
//            else if(!file.canWrite())
//                response.add("Не хватает прав для записи файла");
//        } catch (NullPointerException e){
//            response.add("Не удалось получить информацию об имени файл, возможно переменная окружения SAVEFILE не определена");
//        }
//        catch (Exception e) {
//            response.add("Непредвиденная ошибка");
//            response.add(e.getMessage());
//            response.add(Arrays.toString(e.getStackTrace()));
//        }
//        return new Response(response.toArray());
//
//    }
//
//    @Override
//    public String getHelp() {
//        return "Сохраняет коллекцию в файл, имя которого передается путем чтения значения переменной окружения SAVEFILE, оттуда же загружается коллекция при старте программы";
//    }
//}
