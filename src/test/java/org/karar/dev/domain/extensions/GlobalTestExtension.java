package org.karar.dev.domain.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class GlobalTestExtension implements BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        TestExecutionExceptionHandler {

    private static final String START = "start";

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        final long start = context.getStore(ExtensionContext.Namespace.GLOBAL)
                .remove(START, long.class);

        System.out.println("✅ Done in " +
                (System.currentTimeMillis() - start) + " ms");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put(START, System.currentTimeMillis());
        System.out.println("🚀 " + context.getDisplayName());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (throwable instanceof NullPointerException) {
            throw new AssertionError(
                    "NPE → Check mocks or test setup",
                    throwable
            );
        }

        throw throwable;
    }
}
