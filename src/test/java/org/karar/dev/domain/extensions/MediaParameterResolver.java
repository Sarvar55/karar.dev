package org.karar.dev.domain.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.karar.dev.domain.media.MediaFixture;
import org.karar.dev.domain.media.entity.Media;
import org.karar.dev.domain.media.enums.MediaStatus;

public class MediaParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return pc.getParameter().getType() == Media.class;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {

        boolean active = pc.getParameter().isAnnotationPresent(ActiveMedia.class);
        return active ? MediaFixture.activeMedia() : MediaFixture.pendingMedia();
    }

    /**
     * Optional marker annotation: use on a {@code Media} parameter to receive
     * an ACTIVE media instead of the default PENDING one.
     *
     * <pre>
     *   void myTest(@ActiveMedia Media media) { ... }
     * </pre>
     */
    public @interface ActiveMedia {
    }
}
