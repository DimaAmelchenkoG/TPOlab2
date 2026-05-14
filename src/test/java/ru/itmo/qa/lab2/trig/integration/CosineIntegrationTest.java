package ru.itmo.qa.lab2.trig.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Интеграция: {@link Cosine} вызывает реальный {@link Sine} (cos(x) = sin(π/2 − x)).
 * Проверяем таблично реализацию косинуса вместе с рядом для синуса, без подмены sin на Math.
 */
@ExtendWith(MockitoExtension.class)
public class CosineIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Test
  @DisplayName("Косинус делегирует вычисление синусу")
  void shouldDelegateToSine() {
    Sine spySin = spy(new Sine());
    Cosine cos = new Cosine(spySin);
    cos.calculate(new BigDecimal("9.86"), PRECISION);
    verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @DisplayName("Табличные значения: Cosine + реальный Sine")
  @ParameterizedTest(name = "cos({0}) = {1}")
  @CsvFileSource(resources = "/integration/cosIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldMatchTabularValuesWithRealSine(BigDecimal x, BigDecimal y) {
    Cosine cos = new Cosine(new Sine());
    assertEquals(y, cos.calculate(x, PRECISION));
  }
}
