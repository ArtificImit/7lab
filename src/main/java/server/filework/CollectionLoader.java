package server.filework;

import server.database.Collection;

import java.io.IOException;
import java.text.ParseException;

/**
 * Интерфейс для загрузки сохраненной коллекции
 */
public interface CollectionLoader {
    /**
     * Загружает сохраненную коллекцию
     * @return Загруженная коллекцмя
     * @throws IOException
     * @throws ParseException
     */
    Collection loadCollection() throws IOException, ParseException;
}
