/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.path;

import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fratuz610
 */
public class URIPathTest {
  
  public URIPathTest() {
    
  }

  /**
   * Test of matchAndParse method, of class URIPath.
   */
  @Test
  public void testMatchAndParseSimple() {
    System.out.println("testMatchAndParseSimple");
    
    URIPath path = new URIPath("/hello/:who");
    
    Map<String, String> paramMap = path.matchAndParse("/hello/world");
    
    assertEquals(1, paramMap.size());
    assertEquals(true, paramMap.containsKey("who"));
    assertEquals("world", paramMap.get("who"));
  }
  
  

}
