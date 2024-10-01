package server.filework;

import server.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Класс, реализующий интерфейс FileReader, читает файл при помощи BufferedReader
 * @see FileReader
 */
public class FileInputStreamReader implements FileReader {
    private BufferedReader reader;

    public FileInputStreamReader() {}

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Override
    public void openFile(String fileName) throws IOException {
        String correctPath = "";
        // Убираем квадратные скобки в начале и в конце строки
        if (fileName.startsWith("[") && fileName.endsWith("]")) {
            correctPath =  fileName.substring(1, fileName.length() - 1);
        } else if (fileName.startsWith("[")) {
            correctPath = fileName.substring(1); // Убираем только открывающую скобку
        } else if (fileName.endsWith("]")) {
            correctPath = fileName.substring(0, fileName.length() - 1); // Убираем только закрывающую скобку
        }
        else {
            correctPath = fileName;
        }
        System.out.println("FileName " + correctPath);
        this.reader = new BufferedReader(new java.io.FileReader(correctPath));

    }


    private String readLine() throws IOException {
        return reader.readLine();
    }



    @Override
    public LinkedList<Pair<String, ArrayList<String>>> readCommandsFromFile(String fileName) throws IOException {
        LinkedList<Pair<String, ArrayList<String>>> commandList = new LinkedList<>();
        this.openFile(fileName);

        String command;
        ArrayList<String> argument;
        while ((command = readLine()) != null) {

            argument = new ArrayList<>();
            String[] commandArray = command.split(" ");
            command = commandArray[0];
            for (int i = 0; i < commandArray.length; i++) {
                if (i != 0) {
                    argument.add(commandArray[i]);
                }
            }
            if ("exit".equals(command)) {
                break;
            }
            else {
                commandList.add(new Pair<>(command, argument));
            }
        }
        this.close();
        return commandList;
    }


    @Override
    public String readWholeFile(String fileName) throws IOException {
        this.openFile(fileName);
        StringBuilder res = new StringBuilder();
        String str;
        while ((str = readLine()) != null) {
            res.append(str).append('\n');
        }
        this.close();
        return res.toString();
    }
}
