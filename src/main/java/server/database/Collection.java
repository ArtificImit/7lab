package server.database;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import objectpack.Ticket;

/**
 * Класс коллекции, расширяющий Vector. Основное отличие в хранение и предоставлении даты создания.
 * @param <T> Тип объекта в коллекции, который должен быть Ticket.
 */
@Getter
public class Collection<T extends Ticket> extends Vector<T> implements Serializable{

    /**
     * Дата создания коллекции.
     */
    private Date creationDate;



    /**
     * Поле для хранения билета с минимальной датой создания.
     */
    private T minCreationDateTicket;

    /**
     * Сортирует элементы коллекции по их натуральному порядку (метод compareTo в Ticket).
     */
    public void sortByCompareTo() {
        // Определение компаратора для сортировки по цене
        Comparator<Ticket> comparator = Comparator.comparingInt(Ticket::getPrice);

        // Преобразование Vector в List и сортировка
        List<T> sortedList = this.stream()
                .filter(Objects::nonNull) // Убираем null элементы
                .sorted(comparator)
                .collect(Collectors.toList());

        // Очистка текущей коллекции и добавление отсортированных элементов
        this.clear();
        this.addAll(sortedList); // Используем List вместо LinkedHashSet
    }
    public void reorder() {
        Collections.sort(this, Collections.reverseOrder());
    }
    /**
     * Находит билет по его идентификатору и возвращает объект Ticket.
     * @param id идентификатор билета
     * @return билет, если найден
     * @throws NoSuchElementException если билет с указанным идентификатором не найден
     */
    public Ticket findTicketById(int id) {
        return this.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Билет с идентификатором " + id + " не найден"));
    }
    public Collection(Collection<T> collection) {
        super(collection);
        this.creationDate = new Date();
        updateMinCreationDateTicket();
    }

    public Collection() {
        super();
        this.creationDate = new Date();
    }



    /**
     * Обновляет билет с минимальной датой создания.
     */
    private void updateMinCreationDateTicket() {
        minCreationDateTicket = this.stream()
                .min((t1, t2) -> t1.getCreationDate().compareTo(t2.getCreationDate()))
                .orElse(null);
    }
}
