/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.reffle.jfsdict.exceptions;

import java.io.IOException;

/**
 * This exception is thrown in case of a file format problem
 * @author Ulrich Reffle
 */
public class BadFileException extends IOException {

    public BadFileException() {
        super();
    }
    public BadFileException( String msg) {
        super(msg);
    }
    
}
