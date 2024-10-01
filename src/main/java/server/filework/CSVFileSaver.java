package server.filework;

import objectpack.Ticket;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Класс, реализующий интерфейс FileSaver, сохраняет коллекцию в CSV формате
 */
public class CSVFileSaver implements FileSaver {

    @Override
    public void save(String fileName, Collection<Ticket> arr) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Ticket ticket : arr) {
                writer.write(ticket.toCSV());
                writer.newLine();
            }
        }
    }

}
