По файлам
1. CosineIntegrationTest — табличная проверка new Cosine(new Sine()): реальный ряд синуса, без подмены sin на Math. Отдельно — проверка делегирования через spy(Sine).

2. TangentIntegrationTest — Sine реальный, Cosine — мок с cos(x) из Math (эталон соседа), сравнение с таблицей tanIT.csv. Отдельно — исключение при cos = 0.

3. ecantIntegrationTest — цепочка Secant → Cosine(new Sine()) по таблице secIT.csv; плюс тест, что к Cosine передаётся повышенная точность (scale + 12), и тест на ArithmeticException при нуле косинуса.

4. LogarithmIntegrationTest — параметризованный тест BaseNLogarithm(5, new NaturalLogarithm()) против эталонных значений; отдельно остаётся тест с моком ln, чтобы явно проверить только формулу ln(x)/ln(5) и вызовы в точках x и 5.

5. FunctionSystemIntegrationTest — вместо «всё замокать и сравнить с Math»:

ветка x ≤ 0: реальный Sine, sec / cos / tan — заглушки из Math;
ветка x > 0: реальный NaturalLogarithm, log₂…log₁₀ — заглушки из Math;
ожидаемое значение считается той же формулой варианта, с теми же заранее вычисленными sinV / lnV, что получит и FunctionSystem на тех же экземплярах realSin / realLn (без второго независимого пересчёта ряда). Второй столбец systemIT.csv оставлен только для справки (аргумент теста помечен как неиспользуемый).