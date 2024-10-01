package server.filework;

import server.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Интерфейс для чтения команд из файла
 */
public interface FileReader {

    /**
     * Метод для открытия файла
     * @param fileName имя файла для чтения
     * @throws IOException
     */
    void openFile(String fileName) throws IOException;

    /**
     * Метод, возвращающий список команд, прочитанных из файла
     * @param fileName имя файла для чтения из него команд
     * @return Связный список пар вида (Имя команды; Массив строк для парсинга из них элемента)
     * @throws IOException
     */
    LinkedList<Pair<String, ArrayList<String>>> readCommandsFromFile(String fileName) throws IOException;

    void close() throws IOException;

    /**
     * Метод для чтения всей информации из файла (не в виде команд)
     * @param fileName имя файла для чтения из него команд
     * @return Данные из файла в виде одной строки
     * @throws IOException
     */
    String readWholeFile(String fileName) throws IOException;
}
