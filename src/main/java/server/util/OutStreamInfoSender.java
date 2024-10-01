package server.util;

import java.util.Collection;
/**
 *
 * Класс реализующий интерфейс InfoSender, отправляет информацию в стандартный поток вывода
 * @see InfoSender
 * @author Piromant
 */
public class OutStreamInfoSender implements InfoSender {

    /**
     * Метод для отправки одной строки в стандартный поток вывода
     * @param msg в данном случае коллекция, которую мы хотим вывести
     */
    @Override
    public void sendLine(Object msg){
        System.out.println(msg);
    }

    /**
     * Метод для отправки коллекции в стандартный поток вывода
     * @param msg в данном случае коллекция, которую мы хотим вывести
     */
    @Override
    public void sendMultiLines(Collection msg){
        for(Object o: msg)
            sendLine(o);
    }



}
