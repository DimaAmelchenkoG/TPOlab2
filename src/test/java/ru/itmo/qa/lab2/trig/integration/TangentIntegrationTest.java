package ru.itmo.qa.lab2.trig.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TangentIntegrationTest {
  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Sine mockSin;
  @Mock
  private Cosine mockCos;

  @Spy
  private final Sine spySin = new Sine();
  @Spy
  private final Cosine spyCos = new Cosine(spySin);

  @Test
  @DisplayName("Integration 1: tan depends on sine and cosine")
  void shouldCallSineAndCosineFunctions() {
    Tangent tan = new Tangent(spySin, spyCos);
    tan.calculate(new BigDecimal("9.72"), PRECISION);
    verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @ParameterizedTest(name = "mock.tan({0}) = {1}")
  @DisplayName("Integration 2: tan with tabular stubs")
  @CsvFileSource(resources = "/integration/tanIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldCalculateTangentUsingMockModules(BigDecimal x, BigDecimal y) {
    when(mockSin.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.sin(x.doubleValue())));
    when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.cos(x.doubleValue())));

    Tangent tan = new Tangent(mockSin, mockCos);
    assertEquals(y, tan.calculate(x, PRECISION));
  }

  @Test
  void shouldThrowWhenCosineIsZero() {
    BigDecimal x = BigDecimal.valueOf(Math.PI / 2.0);
    when(mockSin.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.ONE);
    when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.ZERO);

    Tangent tan = new Tangent(mockSin, mockCos);
    assertThrows(ArithmeticException.class, () -> tan.calculate(x, PRECISION));
  }
}
