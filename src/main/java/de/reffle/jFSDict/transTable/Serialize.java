/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jFSDict.transTable;

import java.io.*;

import de.reffle.jFSDict.exceptions.BadFileException;

/**
 *
 * @author uli
 */
public class Serialize {
  public static void writeToStream( RichTransTable tt, FileOutputStream fos ) throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject( tt );
  }

  public static void writeToFile( RichTransTable tt, String filename ) throws IOException {
    FileOutputStream fos = new FileOutputStream(filename);
    writeToStream( tt, fos );
  }

  public static RichTransTable readFromStream( FileInputStream fis ) throws IOException {
    ObjectInputStream ois = new ObjectInputStream( fis );
    RichTransTable tt = null;
    try {
      tt = (RichTransTable)ois.readObject();
      return tt;
    } catch( ClassNotFoundException ex ) {
      throw new BadFileException();
    }

  }

  public static RichTransTable readFromFile( File file ) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    return readFromStream( fis );
  }


}
