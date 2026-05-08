package org.karar.dev.domain.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.domain.extensions.GlobalTestExtension;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({MockitoExtension.class, GlobalTestExtension.class})
public @interface UnitTest {
}
