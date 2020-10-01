package com.dreamtec.bsp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dreamtec.bsp.statement.BankStatementFactory;
import com.dreamtec.bsp.statement.IBankStatement;

/**
 * Bank Statement Parser Engine.<br>
 * 
 * @author chinphek
 */
public class BSPEngine {
    private final List<IBankStatement> statements = new ArrayList<IBankStatement>();

    /**
     * Add file into engine.<br>
     * 
     * @param file
     * @return Class simple name if handler is found, else return null.
     */
    public String add(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("Input 'file' cannot be null.");
        }

        final IBankStatement statement = BankStatementFactory.getHandler(file);
        if (statement != null) {
            statements.add(statement);
            return statement.getAccountName();
        }
        
        return null;
    }

    public void process() {
        for(IBankStatement s : statements) {
            s.process();
        }
    }
}
