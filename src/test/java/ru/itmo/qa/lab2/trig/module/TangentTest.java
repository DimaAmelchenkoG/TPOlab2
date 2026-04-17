package ru.itmo.qa.lab2.trig.module;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TangentTest {
  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");
  private Tangent tan;

  @BeforeEach
  void init() {
    tan = new Tangent();
  }

  @ParameterizedTest(name = "tan({0})")
  @ValueSource(doubles = {-Math.PI, 0, Math.PI})
  void shouldCalculateForPi(double x) {
    assertEquals(ZERO.setScale(PRECISION.scale(), HALF_EVEN), tan.calculate(BigDecimal.valueOf(x), PRECISION));
  }

  @ParameterizedTest
  @ValueSource(doubles = {-Math.PI / 2, Math.PI / 2})
  void shouldNotCalculateForPiHalf(double x) {
    BigDecimal arg = BigDecimal.valueOf(x).setScale(PRECISION.scale(), HALF_EVEN);
    Throwable exception = assertThrows(ArithmeticException.class, () -> tan.calculate(arg, PRECISION));
    String msg = format("У тангенса нет значения при x = %s", arg);
    assertEquals(msg, exception.getMessage());
  }

  @ParameterizedTest(name = "tan({0})")
  @CsvFileSource(resources = "/tan.csv", numLinesToSkip = 1, delimiter = ',')
  void testTan(BigDecimal x, BigDecimal y) {
    MathContext mc = new MathContext(DECIMAL128.getPrecision());
    for (int k = -5; k <= 5; ++k) {
      BigDecimal x_p = BigDecimalMath.pi(mc)
              .multiply(BigDecimal.valueOf(k))
              .add(x);
      assertEquals(y, tan.calculate(x_p, PRECISION));
    }
  }
}
