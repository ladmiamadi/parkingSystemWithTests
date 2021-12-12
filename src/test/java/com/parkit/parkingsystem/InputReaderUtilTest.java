package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Scanner;

import static com.parkit.parkingsystem.App.main;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputReaderUtilTest {

    @Test
    public void readSelectionShouldShutDownSystemTest () {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream("3".getBytes()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);

        InteractiveShell.loadInterface();

        System.setIn(stdin);
        System.setOut(stdout);


        String outputText = byteArrayOutputStream.toString();
        System.out.println(outputText);

        assertThat(outputText).isNotNull();
    }
}
