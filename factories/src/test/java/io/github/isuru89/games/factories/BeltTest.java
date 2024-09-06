package io.github.isuru89.games.factories;

import org.junit.jupiter.api.Test;

class BeltTest {

    @Test
    void testBelt() {
        Belt<String> belt = new Belt<>(30);


        belt.addItem("X", 5);
        System.out.println(belt);
        belt.addItem("Y", 15);
        System.out.println(belt);
        belt.addItem("Z", 10);
        System.out.println(belt);
        System.out.println();
        System.out.println();

        for (int i = 0; i < 30; i++) {
            belt.tick();
            System.out.println(belt);
        }
    }

    @Test
    void testBeltCut() {
        Belt<String> belt = new Belt<>(30);

        belt.addItem("X", 5);
        System.out.println(belt);
        belt.addItem("Y", 15);
        System.out.println(belt);
        belt.addItem("Z", 25);
        System.out.println(belt);
        System.out.println();
        System.out.println();

        var belts = belt.cutAt(2);
        System.out.println(belts.getFirst());
        System.out.println(belts.getLast());
    }

    @Test
    void testBeltCutMiddle() {
        Belt<String> belt = new Belt<>(30);

        belt.addItem("X", 5);
        System.out.println(belt);
        belt.addItem("Y", 15);
        System.out.println(belt);
        belt.addItem("Z", 25);
        System.out.println(belt);
        System.out.println();
        System.out.println();

        var belts = belt.cutAt(10);
        System.out.println(belts.getFirst());
        System.out.println(belts.getLast());
    }

    @Test
    void testBeltCutEnd() {
        Belt<String> belt = new Belt<>(30);

        belt.addItem("X", 5);
        System.out.println(belt);
        belt.addItem("Y", 15);
        System.out.println(belt);
        belt.addItem("Z", 25);
        System.out.println(belt);
        System.out.println();
        System.out.println();

        var belts = belt.cutAt(26);
        System.out.println(belts.getFirst());
        System.out.println(belts.getLast());
    }

}