// p.55: Refactoring + TDD

package calculator;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;

public class StringCalculatorTest {
    StringCalculator cal;

    @Before
    public void setup() {
        cal = new StringCalculator();
    }

    @Test
    public void addNullAndEmpty() {
        assertEquals(0, cal.add(null));
        assertEquals(0, cal.add(""));
    }

    @Test
    public void addOneDigit() {
        assertEquals(1, cal.add("1"));
    }

    @Test(expected = RuntimeException.class)
    public void addNegativeOneDigit() {
        cal.add("-1");
    }

    @Test
    public void addComma() {
        assertEquals(3, cal.add("1,2"));
    }

    @Test
    public void addCommaAndColon() {
        assertEquals(6, cal.add("1,2:3"));
    }

    @Test
    public void addCustomDelimiter() {
        assertEquals(16, cal.add("//;\n1;3;5;7"));
    }

    @Test
    public void addAllDelimiters() {
        assertEquals(16, cal.add("//;\n1:3,5;7"));
    }

    @Test(expected = RuntimeException.class)
    public void addAllDelimitersAndNegative() {
        cal.add("//;\n-1:3,5;7");
    }
}
