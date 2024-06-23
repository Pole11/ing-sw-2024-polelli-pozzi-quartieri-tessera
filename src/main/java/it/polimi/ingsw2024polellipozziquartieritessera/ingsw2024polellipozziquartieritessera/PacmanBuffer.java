package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class PacmanBuffer<T> {
    private Object[] buffer;
    private int size;
    private int cursor; // Index where the next element will be inserted

    public PacmanBuffer(int capacity) {
        this.buffer = new Object[capacity];
        this.size = 0;
        this.cursor = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == buffer.length;
    }

    public int size() {
        return size;
    }

    public void add(T element) {
        if (isFull()) {
            throw new IllegalStateException("Ring buffer is full");
        }
        cursor = (cursor + 1) % buffer.length; // Circular increment
        buffer[cursor] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public void remove(T element) {
        if (isEmpty()) {
            throw new IllegalStateException("Ring buffer is empty");
        }
        for (int i = 0; i < size; i++) {
            if (buffer[i].equals(element)) {
                buffer[i] = buffer[size - 1];
                buffer[size - 1] = null;
                break;
            }
        }
        size--;
    }

    public T getNext() {
        int currentCursor = cursor;
        cursor = (cursor + 1) % buffer.length; // Circular increment
        return (T) buffer[currentCursor];
    }

    public void printStatus() {
        for (int i = 0; i < buffer.length; i++) {
            System.out.println(i + ": " + buffer[i]);
        }
    }
}
