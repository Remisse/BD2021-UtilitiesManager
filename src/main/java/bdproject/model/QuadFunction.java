package bdproject.model;

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

    R apply(T a, U b, V c, W d);
}
