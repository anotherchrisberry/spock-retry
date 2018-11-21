package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

class RetryOnFailureIncludeExcludeSpec extends Specification {
    
    static Integer methodLevelTries = 0
    static Integer thrownTries = 0

    static Integer thrownTries1 = 0
    static Integer methodLevelTries1 = 0

    @RetryOnFailure(times=3, include=IllegalArgumentException.class)
    void 'expect catch on included exception'() {
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

    @RetryOnFailure(include=IllegalArgumentException.class)
    void 'expect thrown on non included exception'() {
        when:
        thrownTries1++
        throw new IllegalStateException('no problem')

        then:
        thrown(IllegalStateException)
        thrownTries1 == 1
    }

    @RetryOnFailure(times=3, exclude=IllegalArgumentException.class)
    void 'expect catch on non excluded exception'() {
        when:
        if (methodLevelTries1 < 2) {
            methodLevelTries1++
            throw new IllegalStateException("have not tried enough times ($methodLevelTries1)")
        }

        then:
        methodLevelTries1 == 2
    }
}
