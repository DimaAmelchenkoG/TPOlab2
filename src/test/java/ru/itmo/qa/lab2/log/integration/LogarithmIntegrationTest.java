package ru.itmo.qa.lab2.log.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Интеграция {@link BaseNLogarithm} с реальным {@link NaturalLogarithm}: log_b(x) = ln(x) / ln(b).
 * Отдельно — заглушка ln, чтобы проверить только деление и вызовы ln в нужных точках.
 */
@ExtendWith(MockitoExtension.class)
class LogarithmIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private NaturalLogarithm mockLn;

  @Test
  @DisplayName("log_5 вызывает реальный ln для x и для основания")
  void shouldCallRealLnForArgumentAndBase() {
    NaturalLogarithm spyLn = spy(new NaturalLogarithm());
    BaseNLogarithm logarithm = new BaseNLogarithm(5, spyLn);
    logarithm.calculate(new BigDecimal("9.93"), new BigDecimal("0.001"));
    verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @DisplayName("log_3(x): реальный ln, сравнение с эталоном")
  @ParameterizedTest(name = "log3({0})")
  @CsvSource({
      "27, 3",
      "3, 1",
      "10, 2.0959033",
  })
  void shouldMatchReferenceWithRealNaturalLog(BigDecimal x, BigDecimal expected) {
    BaseNLogarithm log3 = new BaseNLogarithm(3, new NaturalLogarithm());
    assertEquals(expected.doubleValue(), log3.calculate(x, PRECISION).doubleValue(), PRECISION.doubleValue());
  }

  @Test
  @DisplayName("При фиксированной заглушке ln проверяется формула log_b")
  void shouldCombineStubLnValues() {
    BigDecimal x = new BigDecimal("27");
    when(mockLn.calculate(new BigDecimal("27"), PRECISION)).thenReturn(new BigDecimal("3.295836866"));
    when(mockLn.calculate(new BigDecimal("3"), PRECISION)).thenReturn(new BigDecimal("1.09861228866"));

    BaseNLogarithm log5 = new BaseNLogarithm(3, mockLn);
    BigDecimal expected = new BigDecimal("3");
    assertEquals(expected.doubleValue(), log5.calculate(x, PRECISION).doubleValue(), PRECISION.doubleValue());
  }
}
