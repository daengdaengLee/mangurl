package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.inmemory;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties.RepositoryProperties.Type;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

class InMemoryCondition implements ConfigurationCondition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var beanFactory = context.getBeanFactory();
        if (beanFactory == null) {
            return false;
        }
        var mangurlProperties = beanFactory.getBean(MangurlProperties.class);
        return mangurlProperties.repository().type() == Type.IN_MEMORY;
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}
