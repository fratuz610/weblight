/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.path;

import java.util.Map;
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
  
  /**
   * Test of matchAndParse method, of class URIPath.
   */
  @Test
  public void testMatchAndParseMulti() {
    System.out.println("testMatchAndParseSimple");
    
    URIPath path = new URIPath("/hello/:who/:where/:why");
    
    Map<String, String> paramMap = path.matchAndParse("/hello/me/here/because");
    
    assertEquals(3, paramMap.size());
    assertEquals(true, paramMap.containsKey("who"));
    assertEquals(true, paramMap.containsKey("where"));
    assertEquals(true, paramMap.containsKey("why"));
    assertEquals("me", paramMap.get("who"));
    assertEquals("here", paramMap.get("where"));
    assertEquals("because", paramMap.get("why"));
    
  }
  
  /**
   * Test of matchAndParse method, of class URIPath.
   */
  @Test
  public void testMatchAndParseMultiStar() {
    System.out.println("testMatchAndParseSimple");
    
    URIPath path = new URIPath("/hello/:who/:where/:why/*");
    
    Map<String, String> paramMap = path.matchAndParse("/hello/me/here/because/this-is-an-ending");
    
    assertEquals(4, paramMap.size());
    assertEquals(true, paramMap.containsKey("who"));
    assertEquals(true, paramMap.containsKey("where"));
    assertEquals(true, paramMap.containsKey("why"));
    assertEquals(true, paramMap.containsKey("__star__"));
    assertEquals("me", paramMap.get("who"));
    assertEquals("here", paramMap.get("where"));
    assertEquals("because", paramMap.get("why"));
    assertEquals("this-is-an-ending", paramMap.get("__star__"));
    
  }
  
  /**
   * Test of matchAndParse method, of class URIPath.
   */
  @Test
  public void testMatchAndParseStar1() {
    System.out.println("testMatchAndParseStar1");
    
    URIPath path = new URIPath("/hello/*");
    
    Map<String, String> paramMap = path.matchAndParse("/hello/index.html");
    
    assertEquals(1, paramMap.size());
    assertEquals(true, paramMap.containsKey("__star__"));
    assertEquals("index.html", paramMap.get("__star__"));
  }
  
  /**
   * Test of matchAndParse method, of class URIPath.
   */
  @Test
  public void testMatchAndParseStar2() {
    System.out.println("testMatchAndParseStar2");
    
    URIPath path = new URIPath("/hello/*");
    
    Map<String, String> paramMap = path.matchAndParse("/hello/css/index.css");
    
    assertEquals(1, paramMap.size());
    assertEquals(true, paramMap.containsKey("__star__"));
    assertEquals("css/index.css", paramMap.get("__star__"));
  }

}
