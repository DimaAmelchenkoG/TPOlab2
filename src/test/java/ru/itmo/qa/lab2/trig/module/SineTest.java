package ru.itmo.qa.lab2.trig.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;

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
    double expected = Math.sin(x);
    double actual = sine.calculate(BigDecimal.valueOf(x), PRECISION).doubleValue();
    assertEquals(expected, actual, 1e-6);
  }
}
