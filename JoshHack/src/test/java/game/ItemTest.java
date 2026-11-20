package game;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ItemTest {
    @Test
    void writtenSpellsCanBeReadButNotChangedThroughAccessor() {
        Item spellbook = new Item('?', Color.GRAY, "spellbook", null);
        spellbook.addWrittenSpell("minor heal", 4, new Effect(1));

        List<Spell> spells = spellbook.writtenSpells();
        assertEquals(1, spells.size());
        assertEquals("minor heal", spells.get(0).name());
        assertThrows(UnsupportedOperationException.class, spells::clear);
    }
}
