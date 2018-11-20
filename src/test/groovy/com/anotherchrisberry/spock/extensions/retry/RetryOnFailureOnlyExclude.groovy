package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

class RetryOnFailureOnlyExclude extends Specification {
    
    static Integer methodLevelTries = 0
    static Integer thrownTries = 0

    @RetryOnFailure(times=3, only=IllegalArgumentException.class)
    void 'expect catch on allowed retryable exception'() {
        when:
        if (methodLevelTries < 2) {
            methodLevelTries++
            throw new IllegalArgumentException("have not tried enough times ($methodLevelTries)")
        }

        then:
        methodLevelTries == 2
    }

    @RetryOnFailure(exclude=IllegalArgumentException.class)
    void 'expect thrown on excluded exception'() {
        when:
        thrownTries++
        throw new IllegalArgumentException('no problem')

        then:
        thrown(IllegalArgumentException)
        thrownTries == 1
    }
}