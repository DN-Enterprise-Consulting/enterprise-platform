package de.dn.enterprise.platform.execution.spi;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;

public interface ExecutionAdapter {

    String domainKey();

    ExecutionResult execute(ExecutionContext context);
}
