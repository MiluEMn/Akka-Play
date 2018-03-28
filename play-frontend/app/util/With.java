package util;

@FunctionalInterface
public interface With<T, R> {

  R with(T t);
}
