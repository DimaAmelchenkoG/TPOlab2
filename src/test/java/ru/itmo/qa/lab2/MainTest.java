package ru.itmo.qa.lab2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    Path plotsDir = tempDir.resolve("plots");
    Files.createDirectories(plotsDir);
    Main.setOutputDir(plotsDir.toString());
  }

  @Test
  void shouldGenerateCsvFilesForModules() throws IOException {
    Main.main(new String[] {});

    String[] expectedFiles = {
        "Sine.csv",
        "Cosine.csv",
        "Secant.csv",
        "Tangent.csv",
        "NaturalLogarithm.csv",
        "Log2.csv",
        "Log3.csv",
        "Log5.csv",
        "Log10.csv",
        "FunctionSystem.csv"
    };

    for (String filename : expectedFiles) {
      Path filePath = tempDir.resolve("plots" + File.separator + filename);
      assertTrue(Files.exists(filePath), "Missing file: " + filename);
      assertTrue(Files.size(filePath) > 0, "Empty file: " + filename);
      String head = Files.readString(filePath).lines().findFirst().orElse("");
      assertTrue(head.startsWith("x,y"), filename + " header");
    }
  }
}
