package qgame.map;

import org.junit.Test;

import qgame.state.map.Posn;

import static org.junit.Assert.assertEquals;

/**
 * A test class used to test the POSN class's methods.
 */
public class PosnTest {
  Posn p1 = new Posn(5, 4);
  Posn p2 = new Posn(3, 7);
  Posn p3 = new Posn(5, 4);
  Posn p4 = new Posn(1, 3);


  @Test
  public void testEqualsSymmetric() {
    assertEquals(p1, p3);
    assertEquals(p3, p1);
    Posn newPosn = new Posn(1, 3);
    assertEquals(p4, newPosn);
    assertEquals(newPosn, p4);
  }

  @Test
  public void testEqualsTransitive() {
    assertEquals(p1, p3);
    Posn newPosn = new Posn(5, 4);
    assertEquals(p3, newPosn);
    assertEquals(p1, newPosn);
  }

  @Test
  public void testEqualsReflexive() {
    assertEquals(p1, p1);
    assertEquals(p2, p2);
    assertEquals(p3, p3);
    assertEquals(p4, p4);

  }
}
