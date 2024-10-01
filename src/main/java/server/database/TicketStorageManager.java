package server.database;
import objectpack.Ticket;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
/**
 * Класс коллекции, расширяющий LinkedHashSet. Основное отличие в хранение и предоставлении даты создания
 * @author Piromant
 */
public interface TicketStorageManager<T extends Ticket>  {

    boolean addTicket(T el, String creator);
    boolean update(int id, String userName) throws UserPermissionException;
  //  boolean remove(T el, String userName) throws UserPermissionException;
    Collection getCollection();
   // void clear(String userName);
   // int size();

}