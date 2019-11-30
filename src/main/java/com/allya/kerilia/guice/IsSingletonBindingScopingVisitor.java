package com.allya.kerilia.guice;

import java.lang.annotation.Annotation;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.spi.BindingScopingVisitor;

public class IsSingletonBindingScopingVisitor implements BindingScopingVisitor<Boolean> {
    @Override
    public Boolean visitEagerSingleton() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean visitScope(final Scope scope) {
        return scope == Scopes.SINGLETON;
    }

    @Override
    public Boolean visitScopeAnnotation(final Class<? extends Annotation> scopeAnnotation) {
        return scopeAnnotation == Singleton.class;
    }

    @Override
    public Boolean visitNoScoping() {
        return Boolean.FALSE;
    }
}
