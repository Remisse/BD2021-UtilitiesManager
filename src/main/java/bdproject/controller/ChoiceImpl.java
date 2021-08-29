package bdproject.controller;

import java.util.function.BiFunction;

public class ChoiceImpl<T, V> implements Choice<T, V> {

    private final T item;
    private final V value;
    private final BiFunction<T, V, String> toStringStrategy;

    public ChoiceImpl(final T item, final V value, final BiFunction<T, V, String> strategy) {
        this.item = item;
        this.value = value;
        this.toStringStrategy = strategy;
    }

    @Override
    public T getItem() {
        return item;
    }

    @Override
    public V getValue() {
        return value;
    }

    public String toString() {
        return toStringStrategy.apply(item, value);
    }
}
