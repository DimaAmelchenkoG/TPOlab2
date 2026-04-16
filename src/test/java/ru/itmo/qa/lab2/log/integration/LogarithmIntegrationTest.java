package ru.itmo.qa.lab2.log.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogarithmIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private NaturalLogarithm mockLn;

  @Spy
  private final NaturalLogarithm spyLn = new NaturalLogarithm();

  @Test
  void shouldCallLnInBaseLog() {
    BaseNLogarithm logarithm = new BaseNLogarithm(5, spyLn);
    logarithm.calculate(new BigDecimal("9.93"), new BigDecimal("0.001"));
    verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @Test
  void shouldCalculateWithMockLn() {
    BigDecimal arg = new BigDecimal("1488");
    when(mockLn.calculate(eq(new BigDecimal("1488")), any(BigDecimal.class))).thenReturn(new BigDecimal("7.3051882"));
    when(mockLn.calculate(eq(new BigDecimal("5")), any(BigDecimal.class))).thenReturn(new BigDecimal("1.6094379"));

    BaseNLogarithm log5 = new BaseNLogarithm(5, mockLn);
    BigDecimal expected = new BigDecimal("4.5389687");
    assertEquals(expected, log5.calculate(arg, PRECISION));
  }
}
