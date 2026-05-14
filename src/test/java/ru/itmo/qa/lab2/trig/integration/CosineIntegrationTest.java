package ru.itmo.qa.lab2.trig.integration;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.qa.lab2.trig.Cosine;
import ru.itmo.qa.lab2.trig.Sine;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_EVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CosineIntegrationTest {

    private static final BigDecimal PRECISION = new BigDecimal("0.0000001");

    @Mock
    private Sine mockSin;

    @Spy
    private final Sine spySin = new Sine();



    @Test
    @DisplayName("Integration 1: cod depends on sin")
    void shouldCallSineFunction() {
        Cosine cos = new Cosine(spySin);
        cos.calculate(new BigDecimal("9.86"), PRECISION);
        verify(spySin, atLeastOnce()).calculate(any(BigDecimal.class), any(BigDecimal.class));
    }

    @DisplayName("Integration 2: cos with tabular sin stub")
    @ParameterizedTest(name = "mock.cos({0}) = {1}")
    @CsvFileSource(resources = "/integration/cosIT.csv", numLinesToSkip = 1, delimiter = ',')
    void shouldCalculateCosUsingMockSine(BigDecimal x, BigDecimal y) {
        MathContext mc = new MathContext(PRECISION.add(BigDecimal.valueOf(2)).intValue(), HALF_EVEN);

        BigDecimal piHalf = BigDecimalMath.pi(mc)
                .divide(BigDecimal.valueOf(2), mc.getPrecision(), HALF_EVEN);
        BigDecimal sub = piHalf.subtract(x);
        System.out.println(sub);
        when(mockSin.calculate(any(BigDecimal.class), any(BigDecimal.class))).thenAnswer(invocation -> {
            BigDecimal val = invocation.getArgument(0);
            return BigDecimal.valueOf(Math.sin(val.doubleValue()));
        });
        Cosine cos = new Cosine(mockSin);
        assertEquals(y.doubleValue(), cos.calculate(x, PRECISION).doubleValue(), PRECISION.doubleValue());
    }


}

