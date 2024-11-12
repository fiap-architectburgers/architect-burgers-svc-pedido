package com.example.fiap.archburgers.adapters.testUtils;

import com.example.fiap.archburgers.adapters.datasource.TransactionManager;

import java.util.function.Supplier;

public class DummyTransactionManager implements TransactionManager {
    @Override
    public <T> T runInTransaction(Supplier<T> task) throws Exception {
        return task.get();
    }
}
