package ru.itmo.qa.lab2.log;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.String.format;

public class BaseNLogarithm extends AbstractFunction {
  private final NaturalLogarithm naturalLogarithm;
  private final int base;
  private final BigDecimal baseValue;

  public BaseNLogarithm() {
    this(10);
  }

  public BaseNLogarithm(int base) {
    super();
    this.naturalLogarithm = new NaturalLogarithm();
    this.base = base;
    this.baseValue = BigDecimal.valueOf(base);
  }

  public BaseNLogarithm(int base, NaturalLogarithm naturalLogarithm) {
    super();
    this.naturalLogarithm = naturalLogarithm;
    this.base = base;
    this.baseValue = BigDecimal.valueOf(base);
  }

  public int getBase() {
    return base;
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
    isValid(x, precision);
    if (x.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ArithmeticException(format("Логарифм с основанием %s не имеет значения при x = %s", base, x));
    }
    BigDecimal lnX = naturalLogarithm.calculate(x, precision);
    BigDecimal lnBase = naturalLogarithm.calculate(baseValue, precision);
    return lnX.divide(lnBase, MathContext.DECIMAL128)
            .setScale(precision.scale(), RoundingMode.HALF_EVEN);
  }
}