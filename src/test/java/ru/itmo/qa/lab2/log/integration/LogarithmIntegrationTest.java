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

  @DisplayName("log_5(x): реальный ln, сравнение с эталоном")
  @ParameterizedTest(name = "log5({0})")
  @CsvSource({
      "1488, 4.5389687",
      "5, 1",
      "25, 2",
  })
  void shouldMatchReferenceWithRealNaturalLog(BigDecimal x, BigDecimal expected) {
    BaseNLogarithm log5 = new BaseNLogarithm(5, new NaturalLogarithm());
    assertEquals(expected, log5.calculate(x, PRECISION));
  }

  @Test
  @DisplayName("При фиксированной заглушке ln проверяется формула log_b")
  void shouldCombineStubLnValues() {
    BigDecimal x = new BigDecimal("1488");
    when(mockLn.calculate(new BigDecimal("1488"), PRECISION)).thenReturn(new BigDecimal("7.3051882"));
    when(mockLn.calculate(new BigDecimal("5"), PRECISION)).thenReturn(new BigDecimal("1.6094379"));

    BaseNLogarithm log5 = new BaseNLogarithm(5, mockLn);
    BigDecimal expected = new BigDecimal("4.5389687");
    assertEquals(expected, log5.calculate(x, PRECISION));
  }
}
