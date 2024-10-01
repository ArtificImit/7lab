package server.filework;

import objectpack.Coordinates;
import objectpack.Event;
import objectpack.Ticket;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import objectpack.TicketType;
import server.database.Collection;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, реализующий StorageLoader. Служит для загрузки коллекции из CSV-файла.
 * @see CollectionLoader
 */
public class CSVLoader implements CollectionLoader {
    /**
     * Имя файла, из которого загружается коллекция
     */
    private String fileName;

    public CSVLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Collection<Ticket> loadCollection() throws IOException, IllegalArgumentException {
        Collection<Ticket> collection = new Collection<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                try {
                    Ticket ticket = parseCSVLine(line);
                    collection.add(ticket);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка при чтении элемента, элемент был пропущен: " + e.getMessage());
                }
            }
        }
        return collection;
    }

    /**
     * Преобразует строку CSV в объект Ticket.
     *
     * @param line Строка CSV
     * @return Объект Ticket
     * @throws IllegalArgumentException Если строка CSV неверного формата
     */
    private Ticket parseCSVLine(String line) throws IllegalArgumentException {
        String[] fields = line.split(",");
        if (fields.length < 12) {
            throw new IllegalArgumentException("Недостаточно полей в строке CSV.");
        }

        try {
            Long id = Long.parseLong(fields[0]);
            String name = fields[1];
            TicketType type = TicketType.valueOf(fields[2]);
            Integer price = fields[3].isEmpty() ? null : Integer.parseInt(fields[3]);
            Long discount = Long.parseLong(fields[4]);
            Boolean refundable = fields[5].isEmpty() ? null : Boolean.parseBoolean(fields[5]);
            // Обработка event и coordinates
            Event event = Event.parseEvent(new String[]{fields[6], fields[7], fields[8], fields[9]});
            Coordinates coordinates = Coordinates.parseCoordinates(new String[]{fields[10], fields[11]});
            ZonedDateTime creationDate = ZonedDateTime.parse(fields[12]);

            return new Ticket(id, name, coordinates, creationDate, price, discount, refundable, type, event);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Ошибка при создании объекта Ticket из строки CSV.", e);
        }
    }
}
