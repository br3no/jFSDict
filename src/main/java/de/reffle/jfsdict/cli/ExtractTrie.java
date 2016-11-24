/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jfsdict.cli;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.reffle.jfsdict.transtable.RichTransTable;
import de.reffle.jfsdict.transtable.Serialize;

public class ExtractTrie {

  private static Logger LOG = LoggerFactory.getLogger(ExtractTrie.class);

  public static void main( String[] argv ) {
    try {
      RichTransTable tt = Serialize.readFromFile( new File( argv[0] ) );
      tt.printWordList();
    } catch (IOException ex) {
      LOG.error(ex.toString());
    }
  }
}
