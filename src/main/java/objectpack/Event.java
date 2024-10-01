package objectpack;

import lombok.*;
import server.util.IDGeneratorI;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.zip.DataFormatException;

/**
 * Класс события, который может быть связан с билетами.
 * Событие может иметь уникальный идентификатор, название, дату и тип.
 *
 * @see Ticket
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Serializable {
    /**
     * Уникальный идентификатор события.
     * Значение не может быть null, должно быть больше 0 и уникальным.
     */
    private @NonNull Integer id;


    /**
     * Название события.
     * Значение не может быть null и не может быть пустым.
     */
    private @NonNull String name;

    /**
     * Дата и время события.
     * Это поле может быть null.
     */
    private LocalDateTime date;

    /**
     * Тип события.
     * Значение не может быть null.
     */
    private @NonNull EventType eventType;

    /**
     * Конструктор для инициализации всех полей события.
     *
     * @param name Название события. Не может быть пустым.
     * @param date Дата и время события. Может быть null.
     * @param eventType Тип события. Не может быть null.
     */
    public Event(@NonNull String name, LocalDateTime date, @NonNull EventType eventType) {
        this.name = name;
        this.date = date;
        this.eventType = eventType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Конструктор для создания события по идентификатору.
     *
     * @param id Уникальный идентификатор события. Не может быть null.
     */
    public Event(Integer id) {
        this.id = id;
    }

    /**
     * Проверяет равенство двух объектов Event.
     * Два события считаются равными, если их идентификаторы совпадают.
     *
     * @param o Объект для сравнения с текущим событием.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event)) {
            return false;
        }
        return this.id.equals(((Event) o).getId());
    }

    /**
     * Генерирует хеш-код для события на основе его идентификатора.
     *
     * @return Хеш-код события.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Создает объект Event из массива строковых аргументов.
     *
     * Ожидается, что аргументы будут представлены следующим образом:
     * <ul>
     * <li>args[0] - Название события. Не может быть пустым.</li>
     * <li>args[1] - Дата и время события в формате ISO (например, "2024-09-03T12:00:00"). Может быть пустым.</li>
     * <li>args[2] - Тип события, например "E_SPORTS", "BASEBALL", "EXPOSITION". Не может быть пустым.</li>
     * </ul>
     *
     * @param args Массив строковых аргументов.
     * @return Новый объект Event, созданный из переданных аргументов.
     * @throws IllegalArgumentException Если количество аргументов неверное или аргументы невалидны.
     */
    public static Event parseEvent(String[] args) throws IllegalArgumentException {
        if (args.length < 4 || Arrays.asList(args).contains(null)) {
            throw new IllegalArgumentException("Недостаточно аргументов для создания объекта Event.");
        }

        try {
            Integer id = Integer.parseInt(args[0].trim());

            String name = args[1].trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустой строкой.");
            }

            LocalDateTime date = null;
            if (!args[2].trim().isEmpty()) {
                try {
                    date = LocalDateTime.parse(args[2].trim());
                }
                catch (DateTimeParseException e){
                    date = null;
                }
            }

            EventType eventType = EventType.valueOf(args[3].trim());
            return new Event(id, name, date, eventType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при создании объекта Event.", e);
        }
    }
    public String toCSV() {
        return String.format("%d,%s,%s,%s",
                this.id,
                this.name,
                this.date != null ? this.date : "",
                this.eventType);
    }

}
