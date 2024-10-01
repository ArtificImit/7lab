package server.util;

import objectpack.Event;
import objectpack.Ticket;
import server.database.Collection;

import java.util.Random;

/**
 * Генератор ID для создания уникальных идентификаторов.
 */
public class IDGeneratorL {
    private static Random randomGenerator = new Random();
    private static Collection storage = new Collection();

    /**
     * Генерирует уникальный идентификатор типа Long.
     *
     * @return Уникальный ID.
     */
    public static long generateID() {
        long id;
        do {
            id = randomGenerator.nextInt(20000) + 1;
        } while (storage.contains(new Ticket(id)));
        return id;
    }

    /**
     * Устанавливает хранилище для генератора ID.
     *
     * @param storage Хранилище.
     */
    public static void setStorage(Collection storage) {
        IDGeneratorL.storage = storage;
    }
}
