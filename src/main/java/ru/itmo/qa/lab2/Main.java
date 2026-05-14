package ru.itmo.qa.lab2;

import ru.itmo.qa.lab2.function.AbstractFunction;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;
import ru.itmo.qa.lab2.util.CSVGraphWriter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * ЛР2, вариант 2200. CSV-выгрузка модулей в каталог {@code plots/} (или путь из {@link #setOutputDir(String)}).
 */
public class Main {

  private static String outputDir = System.getProperty("user.dir") + File.separator + "plots" + File.separator;

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
  private static final BigDecimal POSITIVE_END = new BigDecimal("10").setScale(7, HALF_EVEN);
  private static final BigDecimal NEGATIVE_END = POSITIVE_END.negate();
  private static final BigDecimal STEP = new BigDecimal("0.001");

  public static void main(String[] args) {
    try {
      generateFunctionData();
    } catch (IOException e) {
      System.err.println("Ошибка при работе с файлами: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void setOutputDir(String path) {
    outputDir = path.endsWith(File.separator) ? path : path + File.separator;
  }

  private static void write(AbstractFunction f, String stem, BigDecimal from, BigDecimal to) throws IOException {
    new CSVGraphWriter(f, outputDir, stem).write(from, to, STEP, PRECISION);
  }

  private static void generateFunctionData() throws IOException {
    write(new Sine(), null, NEGATIVE_END, POSITIVE_END);
    write(new Cosine(), null, NEGATIVE_END, POSITIVE_END);
    write(new Secant(), null, NEGATIVE_END, POSITIVE_END);
    write(new Tangent(), null, NEGATIVE_END, POSITIVE_END);
    write(new NaturalLogarithm(), null, STEP, POSITIVE_END);
    write(new BaseNLogarithm(2), "Log2", STEP, POSITIVE_END);
    write(new BaseNLogarithm(3), "Log3", STEP, POSITIVE_END);
    write(new BaseNLogarithm(5), "Log5", STEP, POSITIVE_END);
    write(new BaseNLogarithm(10), "Log10", STEP, POSITIVE_END);
    write(new FunctionSystem(), null, NEGATIVE_END, POSITIVE_END);
  }
}
