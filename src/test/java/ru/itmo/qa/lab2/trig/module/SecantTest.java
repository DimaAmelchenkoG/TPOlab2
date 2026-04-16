package ru.itmo.qa.lab2.trig.module;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.itmo.qa.lab2.trig.Secant;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecantTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  private Secant sec;

  @BeforeEach
  void init() {
    sec = new Secant();
  }

  @Test
  void shouldCalculateForMinimum() {
    assertEquals(ONE.setScale(7, HALF_EVEN), sec.calculate(ZERO, PRECISION));
  }

  @Test
  void shouldCalculateForMaximum() {
    MathContext mc = new MathContext(7, HALF_EVEN);
    assertAll(
        () -> assertEquals(ONE.negate().setScale(7, HALF_EVEN),
            sec.calculate(BigDecimalMath.pi(mc).negate(), PRECISION)),
        () -> assertEquals(ONE.negate().setScale(7, HALF_EVEN), sec.calculate(BigDecimalMath.pi(mc), PRECISION)));
  }

  @Test
  void shouldNotCalculateForPiHalf() {
    MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
    BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), mc);
    BigDecimal negativeArg = arg.negate();
    assertAll(
        () -> {
          Throwable exception = assertThrows(ArithmeticException.class, () -> sec.calculate(negativeArg, PRECISION));
          String msg = String.format("У секанса нет значения при x = %s", negativeArg);
          assertEquals(msg, exception.getMessage());
        },
        () -> {
          Throwable exception = assertThrows(ArithmeticException.class, () -> sec.calculate(arg, PRECISION));
          String msg = String.format("У секанса нет значения при x = %s", arg);
          assertEquals(msg, exception.getMessage());
        });
  }

  @ParameterizedTest(name = "sec({0})")
  @CsvFileSource(resources = "/sec.csv", numLinesToSkip = 1, delimiter = ',')
  void testSec(BigDecimal x, BigDecimal y) {
    assertEquals(y, sec.calculate(x, PRECISION));
  }
}
