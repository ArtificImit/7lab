package server.database;


public class UserPermissionException extends Exception{
    public UserPermissionException(String msg){
        super(msg);
    }
    public UserPermissionException(String msg, Throwable cause){
        super(msg, cause);
    }
    public UserPermissionException(){
        super();
    }
}
