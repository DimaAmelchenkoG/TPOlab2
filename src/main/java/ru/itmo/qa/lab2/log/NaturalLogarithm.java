package ru.itmo.qa.lab2.log;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_EVEN;

public class NaturalLogarithm extends AbstractFunction {
  private static final BigDecimal HALF = new BigDecimal("0.5");
  private static final BigDecimal TWO = BigDecimal.valueOf(2);
  private static final BigDecimal LN2 = new BigDecimal(
          "0.693147180559945309417232121458176568075500134360255254120580064096006477419875256948588252");

  public NaturalLogarithm() {
    super();
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
    isValid(x, precision);
    if (x.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ArithmeticException(format("Натуральный логарифм не имеет значения при x = %s", x));
    }
    if (x.compareTo(BigDecimal.ONE) == 0) {
      return BigDecimal.ZERO;
    }

    int workScale = precision.scale() + 10;
    BigDecimal reduced = x;
    int powerOfTwo = 0;
    while (reduced.compareTo(TWO) > 0) {
      reduced = reduced.divide(TWO, workScale, HALF_EVEN);
      powerOfTwo++;
    }
    while (reduced.compareTo(HALF) < 0) {
      reduced = reduced.multiply(TWO).setScale(workScale, HALF_EVEN);
      powerOfTwo--;
    }

    BigDecimal result = lnBySeries(reduced, precision);
    if (powerOfTwo != 0) {
      result = result.add(
              LN2.multiply(BigDecimal.valueOf(powerOfTwo)).setScale(workScale, HALF_EVEN));
    }
    return result.setScale(precision.scale(), HALF_EVEN);
  }

  private BigDecimal lnBySeries(BigDecimal x, BigDecimal precision) {
    if (x.compareTo(BigDecimal.ONE) == 0) {
      return BigDecimal.ZERO;
    }
    int seriesScale = precision.scale() + 10;
    BigDecimal seriesEpsilon = precision.movePointLeft(2);
    BigDecimal z = x.subtract(BigDecimal.ONE).divide(x.add(BigDecimal.ONE), seriesScale, HALF_EVEN);
    BigDecimal z2 = z.pow(2);
    BigDecimal result = BigDecimal.ZERO;
    BigDecimal term = z;
    int i = 1;
    do {
      result = result.add(term.divide(BigDecimal.valueOf(i), seriesScale, HALF_EVEN));
      term = term.multiply(z2);
      i += 2;
    } while (term.abs().compareTo(seriesEpsilon) > 0 && i < getSeriesLength());

    return result.multiply(TWO);
  }
}