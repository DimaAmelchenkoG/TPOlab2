package ru.itmo.qa.lab2.trig.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Два уровня: (1) {@link Secant} + реальный {@link Cosine} (внутри — реальный {@link Sine}).
 * (2) {@link Secant} + заглушка cos — проверка только формулы 1/cos и порога по точности.
 */
@ExtendWith(MockitoExtension.class)
class SecantIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Cosine mockCos;

  @Test
  @DisplayName("Секанс вызывает косинус")
  void shouldDelegateToCosine() {
    Cosine spyCos = spy(new Cosine(new Sine()));
    Secant sec = new Secant(spyCos);
    sec.calculate(new BigDecimal("9.86"), PRECISION);
    verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @DisplayName("Таблица: Secant + реальный Cosine(Sine)")
  @ParameterizedTest(name = "sec({0}) = {1}")
  @CsvFileSource(resources = "/integration/secIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldMatchTabularWithRealCosineChain(BigDecimal x, BigDecimal y) {
    Secant sec = new Secant(new Cosine(new Sine()));
    assertEquals(y, sec.calculate(x, PRECISION));
  }

  @Test
  @DisplayName("Секанс запрашивает cos с увеличенной точностью")
  void shouldCallCosineWithHigherPrecision() {
    BigDecimal x = new BigDecimal("1.1");
    BigDecimal cosArgPrecision = PRECISION.setScale(PRECISION.scale() + 12, HALF_EVEN);
    when(mockCos.calculate(eq(x), eq(cosArgPrecision))).thenReturn(new BigDecimal("0.5"));
    Secant sec = new Secant(mockCos);
    assertEquals(new BigDecimal("2.0000000"), sec.calculate(x, PRECISION));
    verify(mockCos).calculate(eq(x), eq(cosArgPrecision));
  }

  @Test
  void shouldThrowWhenCosineIsZero() {
    BigDecimal x = BigDecimal.valueOf(Math.PI / 2.0);
    BigDecimal cosArgPrecision = PRECISION.setScale(PRECISION.scale() + 12, HALF_EVEN);
    when(mockCos.calculate(eq(x), eq(cosArgPrecision))).thenReturn(BigDecimal.ZERO);

    Secant sec = new Secant(mockCos);
    assertThrows(ArithmeticException.class, () -> sec.calculate(x, PRECISION));
  }
}
