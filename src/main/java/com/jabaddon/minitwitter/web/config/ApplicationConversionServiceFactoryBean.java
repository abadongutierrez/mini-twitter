package com.jabaddon.minitwitter.web.config;

import com.jabaddon.minitwitter.domain.model.MTUser;
import com.jabaddon.minitwitter.domain.repository.MTUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Component;

/**
 * A central place to register application converters and formatters.
 */
@Component
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    private final MTUserRepository _mtUserRepository;

    @Autowired
    public ApplicationConversionServiceFactoryBean(MTUserRepository mtUserRepository) {
        _mtUserRepository = mtUserRepository;
    }

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
    }

    public Converter<MTUser, String> getMTUserToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<MTUser, java.lang.String>() {
            public String convert(MTUser mTUser) {
                return new StringBuilder().append(mTUser.getUsername()).append(' ').append(mTUser.getPassword()).append(' ')
                    .append(mTUser.getPasswordConfirmation()).append(' ').append(mTUser.getName()).toString();
            }
        };
    }

    public Converter<Long, MTUser> getIdToMTUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, MTUser>() {
            public MTUser convert(java.lang.Long id) {
                return _mtUserRepository.findMTUser(id);
            }
        };
    }

    public Converter<String, MTUser> getStringToMTUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, MTUser>() {
            public MTUser convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), MTUser.class);
            }
        };
    }

    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getMTUserToStringConverter());
        registry.addConverter(getIdToMTUserConverter());
        registry.addConverter(getStringToMTUserConverter());
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
