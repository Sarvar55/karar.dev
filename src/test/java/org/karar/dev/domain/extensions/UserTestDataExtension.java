package org.karar.dev.domain.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.UUID;

public class UserTestDataExtension implements
        ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext pc,
                                     ExtensionContext ec) {
        return pc.getParameter().getType() == UUID.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pc,
                                   ExtensionContext ec) {
        return UUID.randomUUID();
    }

}
