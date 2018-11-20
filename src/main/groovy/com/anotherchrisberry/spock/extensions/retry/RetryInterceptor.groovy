package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.util.ReflectionUtil
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import java.util.function.Predicate;


class RetryInterceptor implements IMethodInterceptor {

    static Logger LOG = LoggerFactory.getLogger(RetryInterceptor.class);

    private static final String BEFORE_RETRY_METHOD_NAME = "beforeRetry"

    Integer retryMax
    Double delaySeconds

    /// check whether exception is retryable.
    Predicate<Throwable> checkRetry;

    RetryInterceptor(int retryMax, double delaySeconds, Predicate<Throwable> checkRetry) {
        this.retryMax = retryMax
        this.delaySeconds = delaySeconds
        this.checkRetry = checkRetry
    }

    RetryInterceptor(int retryMax, double delaySeconds) {
        this(retryMax, delaySeconds, {t -> true})
    }

    void intercept(IMethodInvocation invocation) throws Throwable {
        Integer attempts = 0
        while (attempts <= retryMax) {
            try {
                invocation.proceed()
                attempts = retryMax + 1
            } catch (org.junit.AssumptionViolatedException e) {
                throw e
            } catch (Throwable t) {
                if( ! checkRetry.test(t)){
                    throw t
                }
                LOG.info("Retry caught failure ${attempts + 1} / ${retryMax + 1}: ", t)
                attempts++
                if (attempts > retryMax) {
                    throw t
                }
                if (delaySeconds) {
                    Thread.sleep((delaySeconds*1000).toLong())
                }
                invocation.spec.specsBottomToTop.each { spec ->
                    spec.cleanupMethods.each {
                        try {
                            if (it.reflection) {
                                ReflectionUtil.invokeMethod(invocation.target, it.reflection)
                            }
                        } catch (Throwable t2) {
                            LOG.warn("Retry caught failure ${attempts + 1} / ${retryMax + 1} while cleaning up", t2)
                        }
                    }
                }
                invocation.spec.specsTopToBottom.each { spec ->
                    spec.setupMethods.each {
                        try {
                            if (it.reflection) {
                                ReflectionUtil.invokeMethod(invocation.target, it.reflection)
                            }
                        } catch (Throwable t2) {
                            // increment counter, since this is the start of the re-run
                            attempts++
                            if (attempts > retryMax) {
                                throw t
                            }
                            LOG.info("Retry caught failure ${attempts + 1} / ${retryMax + 1} while setting up", t2)
                        }
                    }
                }

                if(invocation.target.respondsTo(BEFORE_RETRY_METHOD_NAME)) {
                    try {
                        invocation.target."$BEFORE_RETRY_METHOD_NAME"()
                    } catch (Throwable t2) {
                        // increment counter, since this is the start of the re-run
                        LOG.info("Retry caught failure when invoking $BEFORE_RETRY_METHOD_NAME ", t2)
                    }
                }
            }
        }
    }
}
