package server.util;

import objectpack.Event;
import objectpack.Ticket;
import server.database.Collection;

import java.util.Random;

/**
 * Генератор ID для создания уникальных идентификаторов.
 */
public class IDGeneratorI {
    private static Random randomGenerator = new Random();
    private static Collection storage = new Collection();

    /**
     * Генерирует уникальный идентификатор типа Integer.
     *
     * @return Уникальный ID.
     */
    public static Integer generateID() {
        Integer id;
        do {
            id = randomGenerator.nextInt(20000);
            id = Math.abs(id) + 1;
        } while (storage.contains(new Event(id)));
        return id;
    }

    /**
     * Устанавливает хранилище для генератора ID.
     *
     * @param storage Хранилище.
     */
    public static void setStorage(Collection storage) {
        IDGeneratorI.storage = storage;
    }
}
