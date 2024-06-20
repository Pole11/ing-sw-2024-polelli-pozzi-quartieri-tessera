package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

public class RingBuffer<T> {
    private Object[] buffer;
    private int size;
    private int front; // Index of the front element
    private int rear; // Index where the next element will be inserted

    public RingBuffer(int capacity) {
        this.buffer = new Object[capacity];
        this.size = 0;
        this.front = 0;
        this.rear = 0;
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
        buffer[rear] = element;
        rear = (rear + 1) % buffer.length; // Circular increment
        size++;
    }

    @SuppressWarnings("unchecked")
    public T remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Ring buffer is empty");
        }
        T removedElement = (T) buffer[front];
        buffer[front] = null; // Clear the reference
        front = (front + 1) % buffer.length; // Circular increment
        size--;
        return removedElement;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Ring buffer is empty");
        }
        return (T) buffer[front];
    }

}
