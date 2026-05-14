package ru.itmo.qa.lab2.function.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.FunctionSystem;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Ветка x ≤ 0: реальный {@link Sine}, остальная тригонометрия — заглушки (эталон Math).
 * Ветка x &gt; 0: реальный {@link NaturalLogarithm}, log_2 … log_10 — заглушки.
 * Ожидаемое значение считается той же формулой, что и в {@link FunctionSystem}, с тем же разделением
 * «наш модуль / заглушка», чтобы тест проверял сборку системы, а не совпадение с полностью замоканным эталоном.
 */
@ExtendWith(MockitoExtension.class)
class FunctionSystemIntegrationTest {

  private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

  @Mock
  private Cosine mockCos;
  @Mock
  private Secant mockSec;
  @Mock
  private Tangent mockTan;
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
    Sine spySin = spy(new Sine());
    Cosine spyCos = spy(new Cosine(spySin));
    Secant spySec = spy(new Secant(spyCos));
    Tangent spyTan = spy(new Tangent(spySin, spyCos));
    NaturalLogarithm spyLn = spy(new NaturalLogarithm());
    BaseNLogarithm spyLog2 = spy(new BaseNLogarithm(2, spyLn));
    BaseNLogarithm spyLog3 = spy(new BaseNLogarithm(3, spyLn));
    BaseNLogarithm spyLog5 = spy(new BaseNLogarithm(5, spyLn));
    BaseNLogarithm spyLog10 = spy(new BaseNLogarithm(10, spyLn));

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
    Sine spySin = spy(new Sine());
    Cosine spyCos = spy(new Cosine(spySin));
    Secant spySec = spy(new Secant(spyCos));
    Tangent spyTan = spy(new Tangent(spySin, spyCos));
    NaturalLogarithm spyLn = spy(new NaturalLogarithm());
    BaseNLogarithm spyLog2 = spy(new BaseNLogarithm(2, spyLn));
    BaseNLogarithm spyLog3 = spy(new BaseNLogarithm(3, spyLn));
    BaseNLogarithm spyLog5 = spy(new BaseNLogarithm(5, spyLn));
    BaseNLogarithm spyLog10 = spy(new BaseNLogarithm(10, spyLn));

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

  @ParameterizedTest(name = "f({0})")
  @DisplayName("Гибрид: реальный sin или ln + заглушки остальных слагаемых")
  @CsvFileSource(resources = "/integration/systemIT.csv", numLinesToSkip = 1, delimiter = ',')
  void shouldMatchHybridAssembly(BigDecimal x, @SuppressWarnings("unused") BigDecimal csvLegacyY) {
    BigDecimal innerPrecision = PRECISION.setScale(PRECISION.scale() + 10, HALF_EVEN);
    MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);

    Sine realSin = new Sine();
    NaturalLogarithm realLn = new NaturalLogarithm();

    stubTrigFromMath(x);
    stubLogsFromMath(x);

    FunctionSystem system = new FunctionSystem(
        realSin, mockCos, mockSec, mockTan, realLn, mockLog2, mockLog3, mockLog5, mockLog10);

    BigDecimal expected;
    if (x.compareTo(ZERO) <= 0) {
      BigDecimal sinV = realSin.calculate(x, innerPrecision);
      expected = expectedNonPositive(sinV, x.doubleValue(), mc);
    } else {
      BigDecimal lnV = realLn.calculate(x, innerPrecision);
      expected = expectedPositive(lnV, x.doubleValue());
    }

    assertEquals(expected, system.calculate(x, PRECISION));
  }

  private void stubTrigFromMath(BigDecimal x) {
    if (x.compareTo(ZERO) > 0) {
      return;
    }
    double xv = x.doubleValue();
    when(mockCos.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.cos(xv)));
    when(mockSec.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(1.0 / Math.cos(xv)));
    when(mockTan.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.tan(xv)));
  }

  private void stubLogsFromMath(BigDecimal x) {
    if (x.compareTo(ZERO) <= 0) {
      return;
    }
    double xv = x.doubleValue();
    when(mockLog2.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(2)));
    when(mockLog3.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(3)));
    when(mockLog5.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.log(xv) / Math.log(5)));
    when(mockLog10.calculate(any(BigDecimal.class), any(BigDecimal.class)))
        .thenReturn(BigDecimal.valueOf(Math.log10(xv)));
  }

  /** Копия ветки x ≤ 0 из {@link FunctionSystem}: sinV — то же значение, что подставит {@link FunctionSystem}. */
  private BigDecimal expectedNonPositive(BigDecimal sinV, double xv, MathContext mc) {
    BigDecimal secV = BigDecimal.valueOf(1.0 / Math.cos(xv));
    BigDecimal cosV = BigDecimal.valueOf(Math.cos(xv));
    BigDecimal tanV = BigDecimal.valueOf(Math.tan(xv));
    return (((((secV.subtract(cosV)).multiply(sinV)).multiply(cosV)).subtract(secV.pow(2, mc)))
            .add(tanV))
        .setScale(PRECISION.scale(), HALF_EVEN);
  }

  /** Копия ветки x &gt; 0: lnV — то же значение, что даст {@link NaturalLogarithm} в системе. */
  private BigDecimal expectedPositive(BigDecimal lnV, double xv) {
    BigDecimal l5 = BigDecimal.valueOf(Math.log(xv) / Math.log(5));
    BigDecimal l2 = BigDecimal.valueOf(Math.log(xv) / Math.log(2));
    BigDecimal l10 = BigDecimal.valueOf(Math.log10(xv));
    BigDecimal l3 = BigDecimal.valueOf(Math.log(xv) / Math.log(3));

    BigDecimal left = (((lnV.subtract(l5)).subtract(l5)).add(l2)).add(l5.add(l5));
    BigDecimal right = (l10.subtract(lnV)).multiply(l3.subtract(lnV));
    return left.subtract(right).setScale(PRECISION.scale(), HALF_EVEN);
  }
}
