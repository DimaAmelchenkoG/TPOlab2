package ru.itmo.qa.lab2.trig.module;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.MathContext.DECIMAL128;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SineTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
  private Sine sine;

  @BeforeEach
  void init() {
    sine = new Sine();
  }

  @ParameterizedTest(name = "sin({0})")
  @ValueSource(doubles = {-1.0, -0.5, 0.0, 0.25, 1.0, 2.0})
  void shouldMatchMathLibrary(double x) {
    for (int k = -5; k <= 5; ++k) {
      double x_p = k * Math.PI * 2 + x;
      double expected = Math.sin(x);
      double actual = sine.calculate(BigDecimal.valueOf(x), PRECISION).doubleValue();
      assertEquals(expected, actual, 1e-6);
    }

  }
}
