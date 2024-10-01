package client;
/**
 *
 * Функциональный интерфейс для проверки аргументов
 */
@FunctionalInterface
public interface ArgumentChecker<String> {
    public boolean check(String arg);
}
