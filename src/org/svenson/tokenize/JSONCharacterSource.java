package org.svenson.tokenize;

import java.io.IOException;

/**
 * Low-level interface to provide character data to the JSON tokenizer. It was introduced
 * to allow for both stream JSON parsing and efficient in-memory parsing.
 *  
 * @author fforw at gmx dot de
 *
 */
public interface JSONCharacterSource
{
    /**
     * Returns the next character or -1 if the end of the character stream was reached.
     * @return
     * @throws IOException 
     */
    int nextChar();

    /**
     * Returns the current character index.
     * 
     * @return
     */
    int getIndex();
    
    /**
     * Allows implementations to close resources.
     */
    void destroy();
}
