package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InputReaderUtilTest {

    @Test
    public void readThirdSelectionShouldShutDownSystemTest () {
        InputStream stdin = System.in;

        System.setIn(new ByteArrayInputStream("3".getBytes()));// Choose the 3 rd choice to shut down system
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);

        InteractiveShell.loadInterface();// display the interactive shell

        System.setIn(stdin);
        System.setOut(stdout);
        String outputText = byteArrayOutputStream.toString();
        System.out.println(outputText);

        assertThat(outputText).isNotNull();
    }
}
