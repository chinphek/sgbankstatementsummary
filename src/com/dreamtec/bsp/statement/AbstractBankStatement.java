package com.dreamtec.bsp.statement;

import java.io.File;

public abstract class AbstractBankStatement {
    protected File file;

    public AbstractBankStatement(File file){
        this.file = file;
    }
}
