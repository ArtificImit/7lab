package QA;

import lombok.Getter;
import lombok.NonNull;
import objectpack.Ticket;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Реквест хранит четыре поля: имя команды, ее аргумент, элемент и отправителя
 */
public class AuthRequest extends Request implements Serializable {
    @Getter
    public final String command_name;
    public final String argument;
    public Ticket element;
    public boolean saveFlag;
    public final boolean sentFromClient;
    @Getter
    @NonNull
    public String userName;
    public int clientId;
    public AuthRequest(Request request, String userName, String passwd, int clientId){
        super();
        this.clientId = clientId;
        element = request.element;
        command_name = request.command_name;
        argument = request.argument;
        this.userName = userName;
        sentFromClient = request.sentFromClient;
        try {
            this.password = hashPasswordMD2(passwd);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
    private String hashPasswordMD2(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD2");
        byte[] digest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));  // Преобразуем каждый байт в шестнадцатеричное представление
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "command_name='" + command_name + '\'' +
                ", argument='" + argument + '\'' +
                ", element=" + element +
                ", sentFromClient=" + sentFromClient +
                ", userName= "+ userName +
                ", password= "+ password +
                '}';
    }
}
