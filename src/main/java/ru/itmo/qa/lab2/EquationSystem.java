package ru.itmo.qa.lab2;

import ru.itmo.qa.lab2.function.AbstractFunction;
import ru.itmo.qa.lab2.log.BaseNLogarithm;
import ru.itmo.qa.lab2.log.NaturalLogarithm;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Secant;
import ru.itmo.qa.lab2.trig.Sine;
import ru.itmo.qa.lab2.trig.Tangent;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * Вариант 2200 (ТПО, ИТМО):
 * <pre>
 * x &lt;= 0 : (((((sec(x) - cos(x)) * sin(x)) * cos(x)) - (sec(x) ^ 2)) + tan(x))
 * x &gt; 0  : (((((ln(x) - log_5(x)) - log_5(x)) + log_2(x)) + (log_5(x) + log_5(x)))
 *            - ((log_10(x) - ln(x)) * (log_3(x) - ln(x))))
 * </pre>
 */
public class EquationSystem extends AbstractFunction {

  private final Sine sin;
  private final Cosine cos;
  private final Secant sec;
  private final Tangent tan;
  private final NaturalLogarithm ln;
  private final BaseNLogarithm log2;
  private final BaseNLogarithm log3;
  private final BaseNLogarithm log5;
  private final BaseNLogarithm log10;

  public EquationSystem() {
    super();
    this.sin = new Sine();
    this.cos = new Cosine();
    this.sec = new Secant();
    this.tan = new Tangent();
    this.ln = new NaturalLogarithm();
    this.log2 = new BaseNLogarithm(2);
    this.log3 = new BaseNLogarithm(3);
    this.log5 = new BaseNLogarithm(5);
    this.log10 = new BaseNLogarithm(10);
  }

  public EquationSystem(
      Sine sin,
      Cosine cos,
      Secant sec,
      Tangent tan,
      NaturalLogarithm ln,
      BaseNLogarithm log2,
      BaseNLogarithm log3,
      BaseNLogarithm log5,
      BaseNLogarithm log10) {
    super();
    this.sin = sin;
    this.cos = cos;
    this.sec = sec;
    this.tan = tan;
    this.ln = ln;
    this.log2 = log2;
    this.log3 = log3;
    this.log5 = log5;
    this.log10 = log10;
  }

  @Override
  public BigDecimal calculate(BigDecimal x, BigDecimal precision) {
    MathContext mc = new MathContext(DECIMAL128.getPrecision(), HALF_EVEN);
    BigDecimal p = precision.setScale(precision.scale() + 10, HALF_EVEN);

    if (x.compareTo(ZERO) <= 0) {
      try {
        BigDecimal secV = c(sec, x, p);
        BigDecimal cosV = c(cos, x, p);
        BigDecimal sinV = c(sin, x, p);
        BigDecimal tanV = c(tan, x, p);
        return (((((secV.subtract(cosV)).multiply(sinV)).multiply(cosV)).subtract(secV.pow(2, mc))).add(tanV))
            .setScale(precision.scale(), HALF_EVEN);
      } catch (ArithmeticException e) {
        throw new ArithmeticException(format("У функции нет значения при x = %s", x));
      }
    }

    try {
      BigDecimal lnV = c(ln, x, p);
      BigDecimal l5 = c(log5, x, p);
      BigDecimal l2 = c(log2, x, p);
      BigDecimal l10 = c(log10, x, p);
      BigDecimal l3 = c(log3, x, p);

      BigDecimal left = (((lnV.subtract(l5)).subtract(l5)).add(l2)).add(l5.add(l5));
      BigDecimal right = (l10.subtract(lnV)).multiply(l3.subtract(lnV));
      return left.subtract(right).setScale(precision.scale(), HALF_EVEN);
    } catch (ArithmeticException e) {
      throw new ArithmeticException(format("У функции нет значения при x = %s", x));
    }
  }

  private BigDecimal c(AbstractFunction function, BigDecimal x, BigDecimal precision) {
    return function.calculate(x, precision);
  }
}
