package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

@RetryOnFailure(times=3)
class RetryOnFailureSpec extends Specification {
    
    static Integer classLevelTries = 0
    static Integer methodLevelTries = 0
    static Integer thrownTries = 0

    void 'class level test'() {
        when:
        if (classLevelTries < 3) {
            classLevelTries++
            throw new RuntimeException("have not tried enough times ($classLevelTries)")
        }

        then:
        classLevelTries == 3
    }

    @RetryOnFailure(times=5)
    void 'method level test'() {
        when:
        if (methodLevelTries < 5) {
            methodLevelTries++
            throw new RuntimeException("have not tried enough times ($methodLevelTries)")
        }

        then:
        methodLevelTries == 5
    }

    @RetryOnFailure(times=7)
    void 'expect thrown is okay'() {
        when:
        thrownTries++
        throw new RuntimeException('no problem')

        then:
        thrown(RuntimeException)
        thrownTries == 1
    }

    void 'mock test'() {
        given:
        Mockable mockable = Mock()

        when:
        if (classLevelTries < 3) {
            classLevelTries++
            throw new RuntimeException("have not tried enough times ($classLevelTries)")
        }
        mockable.doMockyThing()

        then:
        classLevelTries == 3
        1 * mockable.doMockyThing()
        0 * _

    }
}