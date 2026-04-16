package ru.itmo.qa.lab2.function.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.qa.lab2.EquationSystem;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquationSystemTest {

  private static final BigDecimal DEFAULT_PRECISION = new BigDecimal("0.0000001");

  private EquationSystem system;

  @BeforeEach
  void init() {
    system = new EquationSystem();
  }

  @Test
  void shouldNotAcceptNullArgument() {
    assertThrows(NullPointerException.class, () -> system.calculate(null, DEFAULT_PRECISION));
  }

  @Test
  void shouldNotAcceptNullPrecision() {
    BigDecimal arg = new BigDecimal("-2");
    assertThrows(NullPointerException.class, () -> system.calculate(arg, null));
  }

  @ParameterizedTest
  @MethodSource("illegalPrecisions")
  void shouldNotAcceptIncorrectPrecisions(BigDecimal precision) {
    BigDecimal arg = new BigDecimal("-2");
    assertThrows(ArithmeticException.class, () -> system.calculate(arg, precision));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-Math.PI / 2, -3 * Math.PI / 2})
  void shouldRejectAsymptotesOnTrigBranch(double x) {
    BigDecimal arg = BigDecimal.valueOf(x);
    assertThrows(ArithmeticException.class, () -> system.calculate(arg, DEFAULT_PRECISION));
  }

  @ParameterizedTest
  @MethodSource("samplePoints")
  void shouldMatchMathOracle(double x, double tolerance) {
    BigDecimal actual = system.calculate(BigDecimal.valueOf(x), DEFAULT_PRECISION);
    double expected = oracle(x);
    assertTrue(Math.abs(actual.doubleValue() - expected) < tolerance,
        () -> format("x=%s expected≈%s actual=%s", x, expected, actual));
  }

  private static double oracle(double x) {
    if (x <= 0) {
      double sec = 1 / Math.cos(x);
      double c = Math.cos(x);
      double s = Math.sin(x);
      double t = Math.tan(x);
      return ((((sec - c) * s) * c) - sec * sec) + t;
    }
    double ln = Math.log(x);
    double log5 = Math.log(x) / Math.log(5);
    double log2 = Math.log(x) / Math.log(2);
    double log10 = Math.log10(x);
    double log3 = Math.log(x) / Math.log(3);
    double left = (((ln - log5) - log5) + log2) + (log5 + log5);
    double right = (log10 - ln) * (log3 - ln);
    return left - right;
  }

  private static Stream<Arguments> illegalPrecisions() {
    return Stream.of(
        Arguments.of(BigDecimal.valueOf(1)),
        Arguments.of(BigDecimal.valueOf(0)),
        Arguments.of(BigDecimal.valueOf(1.01)),
        Arguments.of(BigDecimal.valueOf(-0.01))
    );
  }

  private static Stream<Arguments> samplePoints() {
    return Stream.of(
        Arguments.of(-5.4, 5e-3),
        Arguments.of(-2.4, 5e-3),
        Arguments.of(-0.1, 5e-3),
        Arguments.of(0.1, 5e-2),
        Arguments.of(1.0, 5e-3),
        Arguments.of(2.0, 5e-3),
        Arguments.of(5.0, 5e-3),
        Arguments.of(1000.0, 2e-1)
    );
  }
}
