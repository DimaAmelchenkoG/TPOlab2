package ru.itmo.qa.lab2.trig;

import ru.itmo.qa.lab2.function.AbstractFunction;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_EVEN;

public class Secant extends AbstractFunction {

  private final Cosine cosine;

  public Secant() {
    super();
    this.cosine = new Cosine();
  }

  public Secant(Cosine cosine) {
    super();
    this.cosine = cosine;
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) throws ArithmeticException {
    isValid(x, precision);
    BigDecimal cos = cosine.calculate(x, precision.setScale(precision.scale() + 12, HALF_EVEN));

    if (cos.abs().compareTo(precision) < 0) {
      throw new ArithmeticException(format("У секанса нет значения при x = %s", x));
    }

    return BigDecimal.ONE.divide(cos, precision.scale(), HALF_EVEN);
  }
}
