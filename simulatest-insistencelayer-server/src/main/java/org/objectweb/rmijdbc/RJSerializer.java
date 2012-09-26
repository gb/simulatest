
package org.objectweb.rmijdbc;

import java.io.*;

/**
 * Utilitary class to "serialize" non-serializable objects, like streams.
 */
public class RJSerializer {

  /**
   * Flush an input stream into a byte array
   */
  public static byte[] toByteArray(InputStream is) throws IOException {
    if(is == null) return null;
    int numAvail = is.available();
    byte[] bytes = new byte[numAvail];
    int readSoFar = 0;
    while (numAvail > 0) {
      int actualRead = is.read (bytes, readSoFar, numAvail);
      readSoFar += actualRead;
      numAvail = is.available();
      if (readSoFar+numAvail > bytes.length) {
        // need to expand the bytes buffer
        byte[] newBytes = new byte[(readSoFar+numAvail)*2];
        System.arraycopy (bytes, 0, newBytes, 0, readSoFar);
        bytes = newBytes;
      } // if
    } // while
    return bytes;
  }

  /**
   * Return an input stream to read a byte array content
   */
  public static InputStream toInputStream(byte[] buf) throws IOException {
    if(buf == null) return null;
    return new ByteArrayInputStream(buf);
  }

  /**
   * Flush a Reader into a char array
   */
  public static char[] toCharArray(Reader reader) throws IOException {
    if(reader == null) return null;
    BufferedReader s = new BufferedReader(reader);
    CharArrayWriter cw = new CharArrayWriter();
    char buf[] = new char[256];
    int br;
    while((br = s.read(buf)) >= 0){
      if (br > 0) cw.write(buf, 0, br);
    }
    s.close();
    return cw.toCharArray();
  }

  /**
   * Return a Reader to read a char array content
   */
  public static Reader toReader(char[] buf) throws IOException {
    if(buf == null) return null;
    return new CharArrayReader(buf);
  }
}

