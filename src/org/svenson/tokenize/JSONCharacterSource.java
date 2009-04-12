package org.svenson.tokenize;

import java.io.IOException;

/**
 * Low-level interface to provide character data to the JSON tokenizer. It was introduced
 * to allow for both stream JSON parsing and efficient in-memory parsing.
 *  
 * @author shelmberger
 *
 */
public interface JSONCharacterSource
{
    /**
     * Returns the next character
     * @return
     * @throws IOException 
     */
    char nextChar();
    /**
     * Returns the complete JSON dataset length in characters.
     * @return
     */
    int getLength();
    /**
     * Returns the current character index.
     * 
     * @return
     */
    int getIndex();
    
    /**
     * 
     */
    void destroy();
}
