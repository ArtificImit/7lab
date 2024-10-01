package server.util;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * Утилитарный класс для хранения двух элементов@EqualsAndHashCode
 */

public class Pair<X, Y> implements Serializable {
    private X first;
    private Y second;
    public Pair(X first, Y second){
        this.first = first;
        this.second = second;
    }

    public X getFirst() {
        return first;
    }

    public void setFirst(X first) {
        this.first = first;
    }

    public void setSecond(Y second) {
        this.second = second;
    }

    public Y getSecond() {
        return second;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // проверка на то, что объекты идентичны
        if (o == null || getClass() != o.getClass()) return false; // проверка на null и совпадение классов
        Pair<?, ?> pair = (Pair<?, ?>) o; // приведение типа

        // Используем Objects.equals для корректного сравнения с учетом null
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public String toString() {
        return first + ", " + second;
    }
}
