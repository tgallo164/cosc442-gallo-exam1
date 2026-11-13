package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CombatRulesTest {

    @Test
    void testCalculateNewHp() {
        assertEquals(70, CombatRules.calculateNewHp(100, 100, -30));
        assertEquals(100, CombatRules.calculateNewHp(100, 100, 40));
        assertEquals(80, CombatRules.calculateNewHp(60, 100, 20));
        assertEquals(-10, CombatRules.calculateNewHp(10, 100, -20));
    }

    @ParameterizedTest
    @CsvSource ({
        "-1, true", "0, true", "50, false", "100, false"
    })
    void testIsDead(int hp, boolean expectedResult) {
        assertEquals(expectedResult, CombatRules.isDead(hp));
    }
}
