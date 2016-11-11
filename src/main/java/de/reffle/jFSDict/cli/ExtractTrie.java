/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jFSDict.cli;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.reffle.jFSDict.transTable.RichTransTable;
import de.reffle.jFSDict.transTable.Serialize;

public class ExtractTrie {
  public static void main( String[] argv ) {
    try {
      RichTransTable tt = Serialize.readFromFile( new File( argv[0] ) );
      tt.printWordList();
    } catch (IOException ex) {
      Logger.getLogger(ExtractTrie.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
