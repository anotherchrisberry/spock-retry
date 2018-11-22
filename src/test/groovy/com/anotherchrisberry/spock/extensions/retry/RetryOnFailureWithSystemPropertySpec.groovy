package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

@RetryOnFailure(times=3)
class RetryOnFailureWithSystemPropertySpec extends Specification {

    static Integer classLevelTries = 0
    static Integer methodLevelTries = 0
    static Integer thrownTries = 0

    static {
        System.setProperty("spock-retry.times", "5")
    }

    void cleanupSpec() {
        System.clearProperty("spock-retry.times")
    }

    void 'class level test'() {
        when:
        if (classLevelTries < 5) {
            classLevelTries++
            throw new RuntimeException("have not tried enough times ($classLevelTries)")
        }

        then:
        classLevelTries == 5
    }

    @RetryOnFailure(times=2)
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

}
