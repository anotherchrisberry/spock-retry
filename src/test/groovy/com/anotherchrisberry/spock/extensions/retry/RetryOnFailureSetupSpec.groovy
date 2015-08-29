package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

@RetryOnFailure(times=2)
class RetryOnFailureSetupSpec extends Specification {

    static Integer setupCalls = 0

    void setup() {
        if (setupCalls < 2) {
            setupCalls++
            throw new RuntimeException("have not tried enough times ($setupCalls)")
        }
    }

    void 'class level test'() {
        given:
        Mockable mockable = Mock()

        when:
        mockable.doMockyThing()

        then:
        setupCalls == 2
        1 * mockable.doMockyThing()
        0 * _
    }
}
