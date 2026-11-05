package com.handbook.creational.objectpool.generic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GenericPoolDemo {

    public static void main(String[] args) throws Exception {
        ObjectPool<StringBuilder> buffers = new ObjectPool<>(2,
                () -> new StringBuilder(8_192), sb -> sb.capacity() >= 8_192, sb -> sb.setLength(0));

        StringBuilder first = buffers.borrow(50, TimeUnit.MILLISECONDS);
        first.append("invoice #1");
        buffers.giveBack(first);

        StringBuilder again = buffers.borrow(50, TimeUnit.MILLISECONDS);
        System.out.println("Reused same buffer? " + (again == first) + ", cleared? " + again.isEmpty());

        StringBuilder second = buffers.borrow(50, TimeUnit.MILLISECONDS);
        try {
            buffers.borrow(50, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Third borrow: " + e.getMessage());
        }
        buffers.giveBack(again);
        buffers.giveBack(second);
        System.out.println("Idle buffers: " + buffers.idleCount());
    }
}
