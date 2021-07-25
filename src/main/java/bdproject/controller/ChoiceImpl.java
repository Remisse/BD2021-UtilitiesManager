package bdproject.controller;

import java.util.function.BiFunction;

public class ChoiceImpl<T, V> implements Choice<T, V> {

    private final T id;
    private final V value;
    private final BiFunction<T, V, String> toStringStrategy;

    public ChoiceImpl(final T id, final V value, final BiFunction<T, V, String> strategy) {
        this.id = id;
        this.value = value;
        this.toStringStrategy = strategy;
    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public V getValue() {
        return value;
    }

    public String toString() {
        return toStringStrategy.apply(id, value);
    }
}
