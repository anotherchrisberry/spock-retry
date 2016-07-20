package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

class DelaySpec extends Specification {

    static Long start = new Date().getTime();
    static Integer methodLevelTries = 0

    @RetryOnFailure(delaySeconds = 1)
    void 'a terrible way to test Thread.sleep'() {
        when:
        if (methodLevelTries < 1) {
            methodLevelTries++
            throw new RuntimeException("have not tried enough times ($methodLevelTries)")
        }
        def duration = (new Date().getTime() - start)

        then:
        methodLevelTries == 1
        duration > 999 && duration < 2000
    }
}
