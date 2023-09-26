package io.github.daengdaenglee.mangurl.outboundadapter.url.repository.condition;

import io.github.daengdaenglee.mangurl.config.properties.MangurlProperties;
import io.github.daengdaenglee.mangurl.config.RepositoryType;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

class RepositoryTypeCondition implements ConfigurationCondition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var beanFactory = context.getBeanFactory();
        if (beanFactory == null) {
            return false;
        }
        var mangurlProperties = beanFactory.getBean(MangurlProperties.class);
        var configRepoType = mangurlProperties.getRepository().getType();

        var annotation = metadata.getAnnotations().get(ConditionalOnRepositoryType.class);
        return annotation.getValue("value")
                .flatMap(repoType -> repoType instanceof RepositoryType ?
                        Optional.of((RepositoryType) repoType) :
                        Optional.empty())
                .map(repoType -> repoType == configRepoType)
                .orElse(false);
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}
