package objectpack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import objectpack.exceptions.CoordinatesException;

import java.io.Serializable;

/**
 * Класс координат в формате (x, y)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates implements Serializable {
    private int x; // Поле не может быть null, меньше 718
    private double y; // Поле может быть null

    @Override
    public String toString(){
        return "(" + x + ", " + y +  ")";
    }

    public String toCSV() {
        return String.format("%d,%.2f", this.x, this.y);
    }

    /**
     * Создает объект Coordinates из строки CSV.
     *
     * @param args массив строк, содержащий координаты.
     * @return Новый объект Coordinates.
     * @throws IllegalArgumentException Если строка неверного формата.
     */
    public static Coordinates parseCoordinates(String[] args) throws IllegalArgumentException, CoordinatesException {
        if (args == null) {
            throw new IllegalArgumentException("Строка координат не может быть null или пустой.");
        }

        if (args.length != 2) {
            throw new CoordinatesException("Неверный формат ввода координат. Правильно: (x, y) x - дробное, меньше 718, y - целое меньше 2^63", 2);
        }


        try {
            int x = Integer.parseInt(args[0].trim());
            if(x > 718){
                throw new NumberFormatException("Значение x больше ожидаемого максимума.");
            }
            double y = Double.parseDouble(args[1].trim());
            return new Coordinates(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка при разборе координат", e);
        }
    }
}
