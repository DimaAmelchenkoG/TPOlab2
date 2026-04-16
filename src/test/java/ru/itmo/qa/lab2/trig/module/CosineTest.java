package ru.itmo.qa.lab2.trig.module;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CosineTest {
  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  private Cosine cos;

  @BeforeEach
  void init() {
    cos = new Cosine(new Sine());
  }

  @Test
  void shouldCalculateForZero() {
    assertEquals(ONE.setScale(7, HALF_EVEN), cos.calculate(ZERO, PRECISION));
  }

  @Test
  void shouldCalculateForPiHalf() {
    MathContext mc = new MathContext(DECIMAL128.getPrecision());
    BigDecimal arg = BigDecimalMath.pi(mc).divide(BigDecimal.valueOf(2), DECIMAL128.getPrecision(), HALF_EVEN);
    BigDecimal expected = ZERO.setScale(7, HALF_EVEN);
    assertAll(
        () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
        () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION)),
        () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3)), PRECISION)),
        () -> assertEquals(expected, cos.calculate(arg.multiply(BigDecimal.valueOf(3)).negate(), PRECISION)));
  }

  @Test
  void shouldCalculateForPi() {
    MathContext mc = new MathContext(DECIMAL128.getPrecision());
    BigDecimal arg = BigDecimalMath.pi(mc);
    BigDecimal expected = ONE.setScale(7, HALF_EVEN).negate();
    assertAll(
        () -> assertEquals(expected, cos.calculate(arg, PRECISION)),
        () -> assertEquals(expected, cos.calculate(arg.negate(), PRECISION)));
  }

  @ParameterizedTest(name = "cos({0})")
  @CsvFileSource(resources = "/cos.csv", numLinesToSkip = 1, delimiter = ',')
  void testCos(BigDecimal x, BigDecimal y) {
    assertEquals(y, cos.calculate(x, PRECISION));
  }
}
