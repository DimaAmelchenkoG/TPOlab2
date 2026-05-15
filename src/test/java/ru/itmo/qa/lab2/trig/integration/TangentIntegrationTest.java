package ru.itmo.qa.lab2.trig.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TangentIntegrationTest {
  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Cosine mockCos;

  @ParameterizedTest(name = "tan({0}) ≈ {1}")
  @DisplayName("Таблица: реальный Sine, cos — заглушка из Math")
  @CsvFileSource(resources = "/integration/tanIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldMatchTabularWithRealSineStubCosine(BigDecimal x, BigDecimal y) {
    Sine realSin = new Sine();
    when(mockCos.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenAnswer(invocation -> {
          BigDecimal arg = invocation.getArgument(0);
          return BigDecimal.valueOf(Math.cos(arg.doubleValue()));
        });

    Tangent tan = new Tangent(realSin, mockCos);
    assertEquals(y, tan.calculate(x, PRECISION));
  }

  @Test
  void shouldThrowWhenCosineIsZero() {
    Sine realSin = new Sine();
    BigDecimal x = BigDecimal.valueOf(Math.PI / 2.0);
    when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.ZERO);

    Tangent tan = new Tangent(realSin, mockCos);
    assertThrows(ArithmeticException.class, () -> tan.calculate(x, PRECISION));
  }
}
