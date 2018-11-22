package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

@RetryOnFailure(times=9)
class RetryOnFailureSuperclassSpec extends Specification {

    static Integer retryCount = 0

    void 'ignores subclass retryCount'() {
        when:
        if (retryCount < 9) {
            retryCount++
            throw new RuntimeException("not there yet: ${retryCount}")
        }

        then:
        retryCount == 9
    }

}
