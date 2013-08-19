/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.holiday69.weblight.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author fratuz610
 */
public class IOHelper {

	public final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	public long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
	}
  
  public String readAsString(InputStream input) throws IOException {
        
    return new String(readAsByteArray(input));
	}
  
  public byte[] readAsByteArray(InputStream input) throws IOException {
    
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
        output.write(buffer, 0, n);
        count += n;
    }
    
    return output.toByteArray();
	}
}
