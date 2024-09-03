package io.github.isuru89.games.shenzenio;

import io.github.isuru89.games.shenzenio.components.MC4000;
import io.github.isuru89.games.shenzenio.components.MC6000;
import io.github.isuru89.games.shenzenio.ports.CaptureSimpleIO;
import io.github.isuru89.games.shenzenio.ports.InputSimpleIO;
import io.github.isuru89.games.shenzenio.ports.InputXBus;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

class GameTest {

    List<Integer> generateNRandomNumbersBetween(int n, int rangeStart, int rangeEnd) {
        var list = new ArrayList<Integer>(n);
        var rnd = new Random(1);

        rnd.ints(n, rangeStart, rangeEnd + 1).forEach(list::add);
        return list;
    }

    @Test
    void runXBusTest() {
        var range1 = Stream.iterate(92, i -> i < 96, i -> i + 1).toList();
        var range2 = Stream.iterate(0, i -> i < 48, i -> i + 1).toList();
        var timeList = new ArrayList<>(range1);
        timeList.addAll(range2);
        var sensorValues = generateNRandomNumbersBetween(timeList.size(), 5, 60);
        var inputSensor = new InputSimpleIO("sensor", sensorValues);
        var inputTime = new InputSimpleIO("time", timeList);
        var inputOnTime = InputXBus.withConstantValue("onTime", 93);
        var inputOffTime = InputXBus.withConstantValue("offTime", 38);

        var comp1 = new MC6000("comp1")
                .addInstructions(InstructionSet.of(
                        " teq x0 p1",
                        "+ mov 1 dat",
                        "  teq x1 p1",
                        "+ mov 0 dat",
                        "  teq dat 1",
                        "- mov 0 x2",
                        "+ tgt p0 19",
                        "+ mov 100 x2",
                        "- mov 0 x2",
                        "  slp 1"
                ));
        var comp2 = new MC4000("comp2")
                .addInstructions(InstructionSet.of(
                        " slx x0",
                        "  mov x0 p0"
                ));

        var game = new Game();
        game.addComponent(comp1);
        game.addComponent(comp2);
        game.addInput(inputTime);
        game.addInput(inputOnTime);
        game.addInput(inputOffTime);
        game.addInput(inputSensor);

        var capture = new CaptureSimpleIO("alarm");

        game.addOutput(capture);

        comp1.getPort("p0").connect(inputSensor);
        comp1.getPort("x0").connect(inputOnTime);
        comp1.getPort("x1").connect(inputOffTime);
        comp1.getPort("p1").connect(inputTime);
        comp1.getPort("x2").connect(comp2.getPort("x0"));
        comp2.getPort("p0").connect(capture);

        var drawer = new Drawer();
        var it = game.iterator(timeList.size());
        while (it.hasNext()) {
//            System.out.println(drawer.snapshot(game));
            it.next();
        }

        var alarmList = capture.getAllValues();

        var expected = Arrays.asList(0, 0, 100, 100, 0, 100, 100, 100, 0, 100, 100, 100, 0, 100, 0, 0, 0, 0, 100, 0, 100, 100, 100, 0, 100, 100, 100, 100, 0, 100, 0, 0, 100, 100, 0, 0, 100, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        Assertions.assertEquals(expected, alarmList);

        System.out.println("Time | Sensor | Alarm |");
        for (int i = 0; i < alarmList.size(); i++) {
            System.out.print(StringUtils.leftPad(String.valueOf(timeList.get(i)), 4) + " |");
            System.out.print(StringUtils.leftPad(String.valueOf(sensorValues.get(i)), 7) + " |");
            System.out.print(StringUtils.leftPad(String.valueOf(alarmList.get(i)), 6) + " |");

            System.out.println();

        }
    }

    @Test
    void runScenarioWithSingleComponent() {
        var comp1 = new MC4000("comp1")
                .addInstructions(InstructionSet.of(
                        "mov 100 p0",
                        "slp 3",
                        "mov 0 p0",
                        "slp 1"
                ));

        var game = new Game();
        game.addComponent(comp1);

        var capture1 = new CaptureSimpleIO("signal-1");
        comp1.getPort("p0").connect(capture1);
        game.addOutput(capture1);

        game.execute(20);

        System.out.println(capture1);
    }

    @Test
    void runScenario1() {
        var comp1 = new MC4000("comp1")
                .addInstructions(InstructionSet.of(
                        "mov 100 p0",
                        "slp 3",
                        "mov 0 p0",
                        "slp 1"
                ));
        var comp2 = new MC4000("comp2")
                .addInstructions(InstructionSet.of(
                        "nop",
                        "mov 100 p0",
                        "slp 1",
                        "mov 0 p0",
                        "slp 2"
                ));

        var game = new Game();
        game.addComponent(comp1);
        game.addComponent(comp2);

        var capture1 = new CaptureSimpleIO("signal-1");
        comp1.getPort("p0").connect(capture1);
        var capture2 = new CaptureSimpleIO("signal-2");
        comp2.getPort("p0").connect(capture2);
        game.addOutput(capture1);
        game.addOutput(capture2);

        game.execute(10);

        System.out.println(capture1);
        System.out.println(capture2);
    }

    @Test
    void runScenarioLinked() {
        var comp1 = new MC4000("comp1")
                .addInstructions(InstructionSet.of(
                        "mov 100 p0",
                        "slp 2",
                        "mov 0 p0",
                        "slp 1"
                ));
        var comp2 = new MC4000("comp2")
                .addInstructions(InstructionSet.of(
                        "nop",
                        "mov p0 p1",
                        "slp 5"
                ));

        var game = new Game();
        game.addComponent(comp1);
        game.addComponent(comp2);

        comp1.getPort("p0").connect(comp2.getPort("p0"));

        var capture1 = new CaptureSimpleIO("signal-1");
        comp1.getPort("p0").connect(capture1);
        var capture2 = new CaptureSimpleIO("signal-2");
        comp2.getPort("p1").connect(capture2);
        game.addOutput(capture1);
        game.addOutput(capture2);

        game.execute(10);

        System.out.println(capture1);
        System.out.println(capture2);
    }

}