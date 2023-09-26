package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.condition;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties.RepositoryProperties.Type;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(RepositoryTypeCondition.class)
public @interface ConditionalOnRepositoryType {
    Type value();
}
