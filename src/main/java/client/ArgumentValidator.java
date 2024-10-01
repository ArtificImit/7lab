package client;

public class ArgumentValidator {

    public static boolean checkId(String[] commandToCheck){
        if(commandToCheck.length == 1){
            System.out.println("ID должен быть введен через пробел после команды");
            return false;
        }
        else if(!commandToCheck[1].matches("[1-9]\\d*")) {
            System.out.println("ID должен быть целым положительным числом");
            return false;
        }
        try {
            Integer.parseInt(commandToCheck[1]);
        } catch (Exception e) {
            System.out.println("Слишком большое значение ID, невозможно выполнить команду");
            return false;
        }
        return true;
    }
    public static boolean checkAuth(String[] commandToCheck){
        if(commandToCheck.length < 2){
            System.out.println("Введите команду в формате login <username> <password>");
            return false;
        }
        else {
            return true;
        }

    }

    public static boolean checkX(String arg){
        try {
            int x = Integer.parseInt(arg);
            return x > 0 && x < 718;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean checkY(String arg){
        try {
            double y = Double.parseDouble(arg);
            return y > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkPrice(String arg){
        try {
            if (arg.trim().isEmpty()) return true; // Price can be null
            Integer price = Integer.parseInt(arg);
            return price > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkDiscount(String arg){
        try {
            Long discount = Long.parseLong(arg);
            return discount > 0 && discount <= 100;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkRefundable(String arg){
        return arg.trim().isEmpty() || arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false");
    }

    public static <T extends Enum<T>> boolean checkType(String arg, Class<T> enumClass) {
        try {
            Enum.valueOf(enumClass, arg.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
