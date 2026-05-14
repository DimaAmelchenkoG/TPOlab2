package ru.itmo.qa.lab2.util;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import static java.lang.String.format;

public class CSVGraphWriter {
  private final BufferedWriter writer;
  private final AbstractFunction function;
  private final String filePath;

  public CSVGraphWriter(AbstractFunction function, String outputDir) {
    this(function, outputDir, null);
  }

  /**
   * @param fileStem если не null — имя файла без расширения (для нескольких модулей с одним классом, например log_a).
   */
  public CSVGraphWriter(AbstractFunction function, String outputDir, String fileStem) {
    this.function = function;
    this.filePath = buildPath(outputDir, function, fileStem);
    this.writer = createWriter();
  }

  public CSVGraphWriter(BufferedWriter writer, String outputDir, AbstractFunction function, String fileStem) {
    this.function = function;
    this.filePath = buildPath(outputDir, function, fileStem);
    this.writer = writer;
  }

  private static String buildPath(String outputDir, AbstractFunction function, String fileStem) {
    String stem = fileStem != null ? fileStem : function.getClass().getSimpleName();
    String dir = outputDir.endsWith(File.separator) ? outputDir : outputDir + File.separator;
    return dir + stem + ".csv";
  }

  public String getFilePath() {
    return filePath;
  }

  private BufferedWriter createWriter() {
    try {
      File file = new File(filePath);
      File parent = file.getParentFile();
      if (parent != null) {
        parent.mkdirs();
      }
      if (!file.exists()) {
        file.createNewFile();
      }
      return new BufferedWriter(new FileWriter(file, false));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void write(BigDecimal x1, BigDecimal x2, BigDecimal d, BigDecimal precision) throws IOException {


    try {
      writer.write("x,y");
      writer.newLine();
      for (BigDecimal i = x1; i.compareTo(x2) <= 0; i = i.add(d)) {
        try {
          BigDecimal y = function.calculate(i, precision);
          writer.write(format(Locale.US, "%s,%s%n", i.toPlainString(), y.toPlainString()));
        } catch (ArithmeticException e) {
          writer.newLine();
        }
      }
    } finally {
      writer.flush();
      writer.close();
    }
  }
}
