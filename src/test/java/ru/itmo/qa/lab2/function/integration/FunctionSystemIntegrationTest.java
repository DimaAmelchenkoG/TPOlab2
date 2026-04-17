package ru.itmo.qa.lab2.function.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.FunctionSystem;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunctionSystemIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Spy
  private final Sine spySin = new Sine();
  @Spy
  private final Cosine spyCos = new Cosine(spySin);
  @Spy
  private final Secant spySec = new Secant(spyCos);
  @Spy
  private final Tangent spyTan = new Tangent(spySin, spyCos);
  @Spy
  private final NaturalLogarithm spyLn = new NaturalLogarithm();
  @Spy
  private final BaseNLogarithm spyLog2 = new BaseNLogarithm(2, spyLn);
  @Spy
  private final BaseNLogarithm spyLog3 = new BaseNLogarithm(3, spyLn);
  @Spy
  private final BaseNLogarithm spyLog5 = new BaseNLogarithm(5, spyLn);
  @Spy
  private final BaseNLogarithm spyLog10 = new BaseNLogarithm(10, spyLn);

  @Mock
  private Sine mockSin;
  @Mock
  private Cosine mockCos;
  @Mock
  private Secant mockSec;
  @Mock
  private Tangent mockTan;
  @Mock
  private NaturalLogarithm mockLn;
  @Mock
  private BaseNLogarithm mockLog2;
  @Mock
  private BaseNLogarithm mockLog3;
  @Mock
  private BaseNLogarithm mockLog5;
  @Mock
  private BaseNLogarithm mockLog10;

  @Test
  void shouldCallTrigModulesForNonPositiveX() {
    FunctionSystem system = new FunctionSystem(
        spySin, spyCos, spySec, spyTan, spyLn, spyLog2, spyLog3, spyLog5, spyLog10);
    system.calculate(new BigDecimal("-5"), PRECISION);
    verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyCos, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spySec, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyTan, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verifyNoInteractions(spyLn);
    verifyNoInteractions(spyLog2);
    verifyNoInteractions(spyLog3);
    verifyNoInteractions(spyLog5);
    verifyNoInteractions(spyLog10);
  }

  @Test
  void shouldCallLogModulesForPositiveX() {
    FunctionSystem system = new FunctionSystem(
        spySin, spyCos, spySec, spyTan, spyLn, spyLog2, spyLog3, spyLog5, spyLog10);
    system.calculate(new BigDecimal("5"), PRECISION);
    verify(spyLn, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog2, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog3, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog5, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verify(spyLog10, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    verifyNoInteractions(spySin);
    verifyNoInteractions(spyCos);
    verifyNoInteractions(spySec);
    verifyNoInteractions(spyTan);
  }

  @ParameterizedTest(name = "f({0}) = {1}")
  @DisplayName("Табличные заглушки + система (эталон Math)")
  @CsvFileSource(resources = "/integration/systemIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldCalculateWithMockFunctions(BigDecimal x, BigDecimal y) {
    if (x.compareTo(ZERO) > 0) {
      double xv = x.doubleValue();
      when(mockLn.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.log(xv)));
      when(mockLog2.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(2)));
      when(mockLog3.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(3)));
      when(mockLog5.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(5)));
      when(mockLog10.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.log10(xv)));
    } else {
      double xv = x.doubleValue();
      when(mockSin.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.sin(xv)));
      when(mockCos.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.cos(xv)));
      when(mockSec.calculate(eq(x), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(1 / Math.cos(xv)));
      when(mockTan.calculate(eq(x), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(Math.tan(xv)));
    }
    FunctionSystem system = new FunctionSystem(
        mockSin, mockCos, mockSec, mockTan, mockLn, mockLog2, mockLog3, mockLog5, mockLog10);
    BigDecimal actual = system.calculate(x, PRECISION);
    assertEquals(0, y.compareTo(actual), () -> "expected " + y + " actual " + actual);
  }
}
