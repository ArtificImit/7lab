package server.database;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import objectpack.*;
import server.util.InfoSender;
import server.util.OutStreamInfoSender;
import server.util.Pair;
import server.util.TicketCreatorPair;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseManager implements TicketStorageManager{
    private final Connection connection;
    private InfoSender infoSender;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<TicketCreatorPair<Ticket, String>> storage;
    private static DatabaseManager db;
    private final String tableName;

    private DatabaseManager(String url, String user, String password, String tableName) throws SQLException {
        this.storage = Collections.synchronizedSet(new LinkedHashSet<>());
        this.connection = DriverManager.getConnection(url, user, password);
        this.tableName = tableName;
        this.loadCollection();
        infoSender = new OutStreamInfoSender();
    }
    @SneakyThrows
    public String[] getAllTicketsAsStringArray() throws SQLException {
        String sql = "SELECT * FROM " + tableName + ";";
        List<String> ticketsList = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                Integer price = rs.getObject("price", Integer.class);
                Timestamp creationDate = rs.getTimestamp("creation_date");
                String owner = rs.getString("creator");

                // Формируем строку для каждого билета
                String ticketString = String.format("Ticket ID: %d, Name: %s, Price: %d, Owner: %s, Creation Date: %s",
                        id, name, price, owner, creationDate);
                ticketsList.add(ticketString);
            }
        }

        // Преобразуем список в массив строк
        return ticketsList.toArray(new String[0]);
    }

    @Override
    @SneakyThrows
    public boolean update(int id, String userName) throws UserPermissionException {
        int updateCount = 0;
        Ticket ticket = getTicketById(id);
        lock.writeLock().lock();
        try {
            try {


                // Проверка прав пользователя
                if (!checkPermission(ticket.getId(), userName)) {
                    throw new UserPermissionException("User does not have permission to update this ticket.");
                }
            }
            catch (NullPointerException e){
                return false;
            }
            // SQL запрос для обновления билета
            PreparedStatement statement = this.connection.prepareStatement(
                    "UPDATE Tickets SET name = ?, price = ?, discount = ?, refundable = ?, type = ?, coordinates_x = ?, coordinates_y = ?, event_id = ? WHERE id = ?"
            );

            // Заполнение параметров запроса
            statement.setString(1, ticket.getName());
            statement.setObject(2, ticket.getPrice(), Types.INTEGER);
            statement.setLong(3, ticket.getDiscount());
            statement.setObject(4, ticket.getRefundable(), Types.BOOLEAN);
            statement.setString(5, ticket.getType().toString());
            statement.setInt(6, ticket.getCoordinates().getX());
            statement.setDouble(7, ticket.getCoordinates().getY());
            statement.setObject(8, ticket.getEvent() != null ? ticket.getEvent().getId() : null, Types.INTEGER);
            statement.setLong(9, ticket.getId());

            // Выполнение обновления
            updateCount = statement.executeUpdate();
        }
        finally {
            lock.writeLock().unlock();
        }

        // Обновление в локальном хранилище, если обновление в базе данных прошло успешно
        if (updateCount > 0) {
            // Удаляем старую версию билета и добавляем обновленную
            this.storage.removeIf(pair -> pair.getFirst().getId().equals(ticket.getId()));
            this.storage.add(new TicketCreatorPair<>(ticket, userName));
            return true; // Успешное обновление
        }

        return false; // Если билет не был найден или обновлен
    }

    @SneakyThrows
    private boolean checkPermission(Long id, @NonNull String userName){
        ResultSet resultSet;
        if(userName.equals("admin"))
            return true;
        lock.readLock().lock();
        try {
            try {
                PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM " + tableName + " WHERE id = ? AND creator = ?");
                statement.setLong(1, id);
                statement.setString(2, userName);
                resultSet = statement.executeQuery();
            }
            finally {
                lock.readLock().unlock();
            }
            if (resultSet.next())
                return Objects.equals(resultSet.getString("creator"), userName);
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }
    @SneakyThrows
    public void clear() {
        lock.writeLock().lock();
        try {
            // SQL запрос для удаления всех записей из таблицы
            String sql = "DELETE FROM " + tableName + ";";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
            String clearEventsSql = "DELETE FROM Events;";
            try (PreparedStatement statement = connection.prepareStatement(clearEventsSql)) {
                statement.executeUpdate();
            }

            // Очистка локального хранилища
            this.storage.clear();
        }
        finally {
            lock.writeLock().unlock();
        }
    }
    public List<TicketCreatorPair<Ticket, String>> getStorage() {
            // Возвращаем копию storage как список
            return new ArrayList<>(storage);
    }
    @SneakyThrows
    public String getDatabaseMetadata() {

        DatabaseMetaData metaData = connection.getMetaData();
        StringBuilder metadataInfo = new StringBuilder();

        metadataInfo.append("Database Product Name: ").append(metaData.getDatabaseProductName()).append("\n");
        metadataInfo.append("Database Product Version: ").append(metaData.getDatabaseProductVersion()).append("\n");
        metadataInfo.append("Driver Name: ").append(metaData.getDriverName()).append("\n");
        metadataInfo.append("Driver Version: ").append(metaData.getDriverVersion()).append("\n");
        metadataInfo.append("User Name: ").append(metaData.getUserName()).append("\n");
        metadataInfo.append("URL: ").append(metaData.getURL()).append("\n");

        return metadataInfo.toString();
    }
    @SneakyThrows
    public Ticket getOldestTicket() {
        Ticket ticket;
        lock.readLock().lock();
        try {
            ticket =storage.stream()
                    .map(TicketCreatorPair::getFirst) // Получаем только билеты
                    .min(Comparator.comparing(Ticket::getCreationDate)) // Находим минимальную дату создания
                    .orElse(null); // Возвращаем null, если коллекция пустая
        }
        finally {
            lock.readLock().unlock();
        }
        return ticket;
    }
    public int removeAllByPrice(int price, String userName) throws SQLException, UserPermissionException {
        String selectQuery = "SELECT id, creator FROM Tickets WHERE price = ?";
        String deleteQuery = "DELETE FROM Tickets WHERE id = ?";
        int affectedRows = 0;
        lock.writeLock().lock();
        try {
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setBigDecimal(1, BigDecimal.valueOf(price));
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        Long ticketId = rs.getLong("id");
                        String creator = rs.getString("creator");

                        // Проверка прав на удаление
                        if (checkPermission(ticketId, userName) || userName.equals("admin")) {
                            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                                deleteStmt.setLong(1, ticketId);
                                affectedRows += deleteStmt.executeUpdate(); // Удаляем билет, если есть права
                            }
                        }
                    }
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        syncCollection();
        return affectedRows; // Возвращаем количество удаленных записей
    }


    @SneakyThrows
    public boolean removeById(Long id, String userName) throws UserPermissionException {
        lock.writeLock().lock();
        try {
            // Проверка прав пользователя
            if (!checkPermission(id, userName)) {
                throw new UserPermissionException(); // Если у пользователя нет прав, бросаем исключение
            }

            String query = "DELETE FROM Tickets WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, id);
                int affectedRows = stmt.executeUpdate(); // Если affectedRows > 0, элемент был удален
                if (affectedRows > 0) {
                    syncCollection();
                    return true;
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    public void syncCollection() throws SQLException {
        // Очистка локальной коллекции
        storage.clear();
        // Загрузка всех элементов из базы данных
        loadCollection();
    }

    public List<Ticket> sortInReverseOrder() {
        lock.writeLock().lock(); // Блокируем для записи
        try {
            // Создаем список для хранения отсортированных билетов
            List<TicketCreatorPair<Ticket, String>> sortedList = new ArrayList<>(storage);

            // Сортируем в обратном порядке по имени
            sortedList.sort((pair1, pair2) -> pair2.getFirst().getName().compareTo(pair1.getFirst().getName()));

            // Обновляем хранилище с отсортированными билетами
            storage.clear();
            storage.addAll(sortedList);

            // Возвращаем список билетов
            List<Ticket> tickets = new ArrayList<>();
            for (TicketCreatorPair<Ticket, String> pair : sortedList) {
                tickets.add(pair.getFirst());
            }

            return tickets; // Возвращаем список билетов
        } finally {
            lock.writeLock().unlock(); // Освобождаем блокировку
        }
    }

    public List<Ticket> sortTicketsByName() {
        lock.writeLock().lock(); // Блокируем для записи
        try {
            List<TicketCreatorPair<Ticket, String>> sortedList = new ArrayList<>(storage);
            sortedList.sort(Comparator.comparing(pair -> pair.getFirst().getName()));

            // Обновляем хранилище с отсортированными билетами
            storage.clear();
            storage.addAll(sortedList);

            // Возвращаем список билетов
            List<Ticket> tickets = new ArrayList<>();
            for (TicketCreatorPair<Ticket, String> pair : sortedList) {
                tickets.add(pair.getFirst());
            }

            return tickets; // Возвращаем список билетов
        } finally {
            lock.writeLock().unlock(); // Освобождаем блокировку
        }
    }





    @SneakyThrows
    public List<Ticket> searchByNameSubstring(String substring) {
        List<Ticket> matchingTickets = new ArrayList<>();
        lock.readLock().lock();
       try {
            for (TicketCreatorPair<Ticket, String> pair : this.storage) {
                Ticket ticket = pair.getFirst();  // Получаем билет из пары
                if (ticket.getName().toLowerCase().contains(substring.toLowerCase())) {
                    matchingTickets.add(ticket);  // Добавляем в список, если имя совпадает с подстрокой
                }
            }
       }
       finally {
           lock.readLock().unlock();
       }
        return matchingTickets;
    }



    @SneakyThrows
    private void loadCollection() {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + ";");
        ResultSet resultSet = statement.executeQuery();
        this.storage.clear();

        while (resultSet.next()) {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            Coordinates coordinates = new Coordinates(resultSet.getInt("coordinates_x"), resultSet.getDouble("coordinates_y"));
            Timestamp creationTimestamp = resultSet.getTimestamp("creation_date");
            ZonedDateTime creationDate = creationTimestamp.toInstant().atZone(ZoneOffset.UTC);
            Integer price = resultSet.getObject("price", Integer.class);
            Long discount = resultSet.getLong("discount");
            Boolean refundable = resultSet.getBoolean("refundable");
            TicketType type = TicketType.valueOf(resultSet.getString("type"));
            Integer eventId = resultSet.getObject("event_id", Integer.class);
            String owner = resultSet.getString("creator");

            Event event = null;
            if (eventId != null) {
                event = getEventById(eventId);
            }

            Ticket ticket = new Ticket(id, name, coordinates, creationDate, price, discount, refundable, type, event);
            this.storage.add(new TicketCreatorPair<>(ticket, owner));
        }
    }
    @Override
    public Collection getCollection() {
        return new LinkedHashSet<Pair<Ticket, String>>(this.storage);
    }
    public static DatabaseManager getAccess(String url, String user, String password, String tableName) throws SQLException{
        if(db == null)
            db = new DatabaseManager(url, user, password, tableName);
        return db;
    }
    // Метод для добавления нового билета в базу данных
    public boolean addTicket(Ticket ticket, String creator) {
        String sql = "INSERT INTO Tickets (id, name, creation_date, price, discount, refundable, type, coordinates_x, coordinates_y, event_id, creator) VALUES (?, ?, now(), ?, ?, ?, ?, ?, ?, ?, ?)";
        lock.writeLock().lock();
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(2, ticket.getName());
                stmt.setObject(3, ticket.getPrice(), Types.INTEGER);
                stmt.setLong(4, ticket.getDiscount());
                stmt.setObject(5, ticket.getRefundable(), Types.BOOLEAN);
                stmt.setString(6, ticket.getType().toString());
                stmt.setInt(7, ticket.getCoordinates().getX());
                stmt.setDouble(8, ticket.getCoordinates().getY());

                // Сначала добавьте событие и получите его сгенерированный ID
                Integer eventId = 0;
                if (ticket.getEvent() != null) {
                    eventId = addEvent(ticket.getEvent());
                    ticket.getEvent().setId(eventId); // Присваиваем ID событию
                }

                // Устанавливаем event_id и id билета как eventId
                stmt.setObject(1, eventId, Types.OTHER); // Устанавливаем event_id
                stmt.setObject(9, eventId, Types.INTEGER); // Устанавливаем id (если это нужно)

                stmt.setString(10, creator); // Устанавливаем создателя

                // Выполнение запроса и получение сгенерированного ключа
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    return false; // Если не добавлено ни одной записи
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setId(generatedKeys.getLong(1)); // Присваиваем сгенерированный ID билету
                    }
                }

                storage.add(new TicketCreatorPair<>(ticket, creator)); // Добавляем в локальное хранилище
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    // Метод для добавления нового события в базу данных и получения его сгенерированного ID
    public Integer addEvent(Event event) throws SQLException {
        String sql = "INSERT INTO Events (name, date, event_type) VALUES (?, ?, ?)";
        lock.writeLock().lock();
        try {

            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, event.getName());
                stmt.setObject(2, event.getDate(), Types.TIMESTAMP);
                stmt.setString(3, event.getEventType().toString());
                stmt.executeUpdate();

                // Получение сгенерированного ID события
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Возвращаем сгенерированный ID события
                    } else {
                        throw new SQLException("Не удалось получить сгенерированный ID события.");
                    }
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }





    // Метод для получения события по ID
    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM Events WHERE id = ?";
        lock.readLock().lock();
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, eventId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Event(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null,
                                EventType.valueOf(rs.getString("event_type"))
                        );
                    }
                }
            }
        }
        finally {
            lock.readLock().unlock();
        }
        return null;
    }
    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT * FROM Tickets WHERE id = ?";
        lock.readLock().lock();
        try {

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, ticketId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Ticket(
                                rs.getLong("id"),
                                rs.getString("name"),
                                new Coordinates(
                                        rs.getInt("coordinates_x"),
                                        rs.getDouble("coordinates_y")
                                ),
                                rs.getTimestamp("creation_date") != null ? rs.getTimestamp("creation_date").toLocalDateTime().atZone(Ticket.ZONE_OFFSET) : null,

                                rs.getInt("price"),
                                rs.getLong("discount"),
                                rs.getBoolean("refundable"),
                                TicketType.valueOf(rs.getString("type")),
                                getEventById(rs.getInt("event_id")) // Получение связанного события
                        );
                    }
                }
            }
        }
        finally {
            lock.readLock().unlock();
        }
        return null;
    }
    // Метод для получения списка всех билетов
    public List<Ticket> getAllTickets() throws SQLException {
        String sql = "SELECT * FROM Tickets";
        List<Ticket> tickets = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getLong("id"));
                ticket.setName(rs.getString("name"));
                ticket.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime().atZone(Ticket.ZONE_OFFSET));
                ticket.setPrice(rs.getObject("price", Integer.class));
                ticket.setDiscount(rs.getLong("discount"));
                ticket.setRefundable(rs.getObject("refundable", Boolean.class));
                ticket.setType(TicketType.valueOf(rs.getString("type")));
                Coordinates coordinates = new Coordinates(rs.getInt("coordinates_x"), rs.getDouble("coordinates_y"));
                ticket.setCoordinates(coordinates);

                Integer eventId = rs.getObject("event_id", Integer.class);
                if (eventId != null) {
                    ticket.setEvent(getEventById(eventId));
                }

                tickets.add(ticket);
            }
        }

        return tickets;
    }




}
