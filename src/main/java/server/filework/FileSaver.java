package server.filework;

import objectpack.Ticket;
import java.io.IOException;
import java.util.Collection;
/**
 *
 * Интерфейс для сохранения коллекции
 */
public interface FileSaver {
    /**
     * Метод сохраняющий коллекцию в файл в формате
     * @param fileName имя файла, в который будет идти сохранение
     * @param arr колекция, которая будет сохранена
     * @throws IOException
     * @throws SecurityException
     */
    void save(String fileName, Collection<Ticket> arr) throws IOException;
}
