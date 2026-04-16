package ru.itmo.qa.lab2.log.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.itmo.qa.lab2.log.BaseNLogarithm;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseNLogarithmTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
  private BaseNLogarithm log5;

  @BeforeEach
  void init() {
    log5 = new BaseNLogarithm(5);
  }

  @Test
  void shouldNotCalculateForZero() {
    assertThrows(ArithmeticException.class, () -> log5.calculate(ZERO, PRECISION));
  }

  @Test
  void shouldCalculateForOne() {
    assertEquals(ZERO.setScale(7, HALF_EVEN), log5.calculate(ONE, PRECISION));
  }

  @ParameterizedTest(name = "log5({0})")
  @CsvFileSource(resources = "/log5.csv", numLinesToSkip = 1, delimiter = ',')
  void testLog(BigDecimal x, BigDecimal y) {
    assertEquals(y, log5.calculate(x, PRECISION));
  }
}
