package QA;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import objectpack.Ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Реквест хранит четыре поля: имя команды, ее аргумент, элемент и отправителя
 */
@AllArgsConstructor
public class Request implements Serializable {
    @Getter
    public String command_name;
    public String argument;
    @Getter
    public Ticket element;
    public boolean saveFlag;
    public boolean sentFromClient;
    @Getter
    public String userName;
    public String password;
    public Request(String command, ArrayList<String> element, boolean sentFromClient) {
        String[] commandParts = command.split(" ");

        this.command_name = commandParts[0];

        Ticket ticket = null;
        if(element.size()!= 0) {
            if (Objects.equals(command_name, "add")) {
                ticket = Ticket.parseTicket(element.toArray(new String[10]));
            }
            if (!Objects.equals(command_name, "login") || !Objects.equals(command_name, "register") ){
                this.element = ticket;
            }

        }

        if(commandParts.length == 2)
            this.argument = commandParts[1];
        else if(commandParts.length == 3 && Objects.equals(command_name, "login") || Objects.equals(command_name, "register")){
            this.argument = commandParts[1] + " " + commandParts[2];
        }
        else
            this.argument = null;


        this.sentFromClient = sentFromClient;


    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Request() {

    }

    @Override
    public String toString() {
        return "Request{" +
                "command_name='" + command_name + '\'' +
                ", argument='" + argument + '\'' +
                ", element=" + element +
                ", sentFromClient=" + sentFromClient +
                '}';
    }
}
