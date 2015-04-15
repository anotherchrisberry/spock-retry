package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.util.ReflectionUtil

class RetryInterceptor implements IMethodInterceptor {

    Integer retryMax

    RetryInterceptor(int retryMax) {
        this.retryMax = retryMax
    }

    void intercept(IMethodInvocation invocation) throws Throwable {
        Integer attempts = 0
        while (attempts <= retryMax) {
            try {
                invocation.proceed()
                attempts = retryMax + 1
            } catch (Throwable t) {
                attempts++
                if (attempts > retryMax) {
                    throw t
                }
                invocation.spec.cleanupMethods.each {
                    if (it.reflection) {
                        ReflectionUtil.invokeMethod(invocation.target, it.reflection)    
                    }
                }
                invocation.spec.setupMethods.each {
                    if (it.reflection) {
                        ReflectionUtil.invokeMethod(invocation.target, it.reflection)
                    }
                }
            }
        }
    }
}
