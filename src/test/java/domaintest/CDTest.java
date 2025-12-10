package domaintest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.CD;

import java.time.LocalDate;

class CDTest {

    @Test
    void testDefaultConstructor() {
        CD cd = new CD();
        assertNotNull(cd);

        // Media inherited fields start null/false
        assertNull(cd.getId());
        assertNull(cd.getTitle());
        assertNull(cd.getArtist());
        assertFalse(cd.isBorrowed());
        assertNull(cd.getDueDate());
    }

    @Test
    void testParameterizedConstructor() {
        CD cd = new CD("CD001", "Chill Vibes", "Lo-Fi Artist");

        assertEquals("CD001", cd.getId());
        assertEquals("Chill Vibes", cd.getTitle());
        assertEquals("Lo-Fi Artist", cd.getArtist());
    }

    @Test
    void testArtistSetterGetter() {
        CD cd = new CD();
        cd.setArtist("New Artist");
        assertEquals("New Artist", cd.getArtist());
    }

    @Test
    void testInheritedIdTitleSetters() {
        CD cd = new CD();
        cd.setId("NEWID");
        cd.setTitle("New Title");

        assertEquals("NEWID", cd.getId());
        assertEquals("New Title", cd.getTitle());
    }

    @Test
    void testSetBorrowedAndGetBorrowed() {
        CD cd = new CD();
        assertFalse(cd.isBorrowed());

        cd.setBorrowed(true);
        assertTrue(cd.isBorrowed());

        cd.setBorrowed(false);
        assertFalse(cd.isBorrowed());
    }

    @Test
    void testSetAndGetDueDate() {
        CD cd = new CD();
        LocalDate date = LocalDate.now().plusDays(3);

        cd.setDueDate(date);
        assertEquals(date, cd.getDueDate());
    }

    @Test
    void testBorrowAt_SetsBorrowedFlagAndDueDate() {
        CD cd = new CD("CD777", "Ocean Sounds", "Nature Band");

        LocalDate today = LocalDate.now();
        cd.borrowAt(today);

        assertTrue(cd.isBorrowed());
        assertEquals(today.plusDays(cd.getBorrowPeriod()), cd.getDueDate());
    }

    @Test
    void testMarkReturned_ClearsBorrowedAndDueDate() {
        CD cd = new CD("CD888", "Storm Waves", "Nature Band");

        cd.borrowAt(LocalDate.now());
        assertTrue(cd.isBorrowed());
        assertNotNull(cd.getDueDate());

        cd.markReturned();

        assertFalse(cd.isBorrowed());
        assertNull(cd.getDueDate());
    }

    @Test
    void testBorrowPeriodValue() {
        CD cd = new CD();
        assertEquals(7, cd.getBorrowPeriod());
    }

    @Test
    void testFinePerDayValue() {
        CD cd = new CD();
        assertEquals(20.0, cd.getFinePerDay());
    }
}
