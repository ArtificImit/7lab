package objectpack;

import lombok.*;
import objectpack.exceptions.TicketException;
import server.util.IDGeneratorL;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.zip.DataFormatException;

/**
 * Класс билета, объекты которого являются элементами коллекции Collection.
 * @see server.database.Collection
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements Comparable<Ticket>, Serializable {
    private @NonNull Long id; // Поле не может быть null, Значение этого поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно быть генерироваться автоматически
    private @NonNull String name; // Поле не может быть null, Строка не может быть пустой
    private @NonNull Coordinates coordinates; // Поле не может быть null
    private @NonNull ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно быть генерироваться автоматически
    private Integer price; // Поле может быть null, Значение поля должно быть больше 0
    private @NonNull Long discount; // Поле не может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 100
    private Boolean refundable; // Поле может быть null
    private @NonNull TicketType type; // Поле не может быть null
    private Event event; // Поле может быть null
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    public Ticket(@NonNull String name, @NonNull Coordinates coordinates, Integer price, @NonNull Long discount, Boolean refundable, @NonNull TicketType type, Event event) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.price = price;
        this.discount = discount;
        this.refundable = refundable;
        this.type = type;
        this.event = event;

        if (discount <= 0 || discount > 100) {
            throw new IllegalArgumentException("Скидка должна быть больше 0 и меньше или равна 100.");
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket(Long id) {
        this.id = id;
    }

    public int compareTo(Ticket other) {
        if (other == null) {
            return 1;
        }
        return this.name.compareTo(other.getName());
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ticket)) {
            return false;
        }
        return this.id.equals(((Ticket) o).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Создает объект Ticket из массива строковых аргументов.
     *
     * Ожидается, что аргументы будут представлены следующим образом:
     * <ul>
     * <li>args[0] - Название билета. Не может быть пустым.</li>
     * <li>args[1] - Координаты в формате "x y". Например, "10 20".</li>
     * <li>args[2] - Цена билета. Может быть пустым (null).</li>
     * <li>args[3] - Скидка на билет. Должна быть числом больше 0 и меньше или равна 100.</li>
     * <li>args[4] - Возможность возврата билета. Должна быть "true" или "false", может быть пустым (null).</li>
     * <li>args[5] - Тип билета. Например, "VIP", "USUAL", "BUDGETARY", "CHEAP".</li>
     * <li>args[6] (необязательный) - Название события, связанного с билетом. Может быть пустым.</li>
     * <li>args[7] (необязательный) - Тип события, связанного с билетом. Например, "E_SPORTS", "BASEBALL", "EXPOSITION".</li>
     * </ul>
     *
     * @param args Массив строковых аргументов.
     * @return Новый объект Ticket, созданный из переданных аргументов.
     * @throws IllegalArgumentException Если количество аргументов неверное или аргументы невалидны.
     */
    public static Ticket parseTicket(String[] args) throws IllegalArgumentException{
        if (args.length < 10) {
            throw new IllegalArgumentException("Недостаточно аргументов для создания объекта Ticket.");
        }

        try {
            String name = args[0].trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустой строкой.");
            }

            String[] coordArgs = {args[1], args[2]};
            Coordinates coordinates = Coordinates.parseCoordinates(coordArgs);



            Integer price = args[3].trim().isEmpty() ? null : Integer.parseInt(args[3].trim());
            Long discount = Long.parseLong(args[4].trim());
            Boolean refundable = args[5].trim().isEmpty() ? null : Boolean.parseBoolean(args[5].trim());
            TicketType type = TicketType.valueOf(args[6].trim());

            Event event = null;
                EventType eventType = EventType.valueOf(args[7].trim());
                String eventName = args[8].trim();
                LocalDateTime date = null;
                try {
                    date = LocalDateTime.parse(args[9].trim());
                }
                catch (DateTimeParseException e){
                    date = null;
                }
                event = new Event(eventName, date, eventType);


            return new Ticket(name, coordinates, price, discount, refundable, type, event);
        } catch (Exception e) {

            throw new IllegalArgumentException("Ошибка при создании объекта Ticket." + "\n" + Arrays.toString(e.getStackTrace()), e);
        }
    }
    /**
     * Возвращет строку которая пойдёт в файл
     * 0 ID, 1 name, 2 TType, 3 price, 4 discount, 5 refundable, 6 EID, 7 EDame, 8 EDate, EType, 9 x, 10 y, 11 creationDate
     * */
    public String toCSV() {
        return String.format("%d,%s,%s,%d,%d,%s,%d,%s,%s,%s,%d,%s,%s",
                this.id,
                this.name,
                this.type,
                this.price != null ? this.price : 1,
                this.discount,
                this.refundable != null ? this.refundable : "",
                this.event.getId(),
                this.event.getName(),
                this.event.getDate(),
                this.event.getEventType(),
                this.coordinates.getX(),
                this.coordinates.getY(),
                this.creationDate);
    }

    public String eventToCSV() {
        return this.event != null ? this.event.toCSV() : "";
    }

    public String coordinatesToCSV() {
        return this.coordinates != null ? this.coordinates.toCSV() : "";
    }

}
