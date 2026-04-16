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
import ru.itmo.qa.lab2.trig.Secant;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecantIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Cosine mockCos;

  @Spy
  private final Cosine spyCos = new Cosine();

  @Test
  @DisplayName("Integration 1: sec depends on cosine")
  void shouldCallCosineFunction() {
    Secant sec = new Secant(spyCos);
    sec.calculate(new BigDecimal("9.86"), PRECISION);
    verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
  }

  @DisplayName("Integration 2: sec with tabular cosine stub")
  @ParameterizedTest(name = "mock.sec({0}) = {1}")
  @CsvFileSource(resources = "/integration/secIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldCalculateSecantUsingMockCosine(BigDecimal x, BigDecimal y) {
    when(mockCos.calculate(eq(x), eq(PRECISION.setScale(PRECISION.scale() + 12, HALF_EVEN))))
        .thenReturn(BigDecimal.valueOf(Math.cos(x.doubleValue())));
    Secant sec = new Secant(mockCos);
    assertEquals(y, sec.calculate(x, PRECISION));
  }
}
