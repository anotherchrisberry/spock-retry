package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.util.ReflectionUtil
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class RetryInterceptor implements IMethodInterceptor {

    static Logger LOG = LoggerFactory.getLogger(RetryInterceptor.class);

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
                LOG.info("Retry caught failure ${attempts + 1} / ${retryMax + 1}: ", t)
                attempts++
                if (attempts > retryMax) {
                    throw t
                }
                invocation.spec.cleanupMethods.each {
                    try {
                        if (it.reflection) {
                            ReflectionUtil.invokeMethod(invocation.target, it.reflection)
                        }
                    } catch (Throwable t2) {
                        LOG.warn("Retry caught failure ${attempts + 1} / ${retryMax + 1} while cleaning up", t2)
                    }
                }
                invocation.spec.setupMethods.each {
                    try {
                        if (it.reflection) {
                            ReflectionUtil.invokeMethod(invocation.target, it.reflection)
                        }
                    } catch (Throwable t2) {
                        // increment counter, since this is the start of the re-run
                        attempts++
                        LOG.info("Retry caught failure ${attempts + 1} / ${retryMax + 1} while setting up", t2)
                    }
                }
            }
        }
    }
}
