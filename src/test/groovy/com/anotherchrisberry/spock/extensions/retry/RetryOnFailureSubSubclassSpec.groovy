package com.anotherchrisberry.spock.extensions.retry

class RetryOnFailureSubSubclassSpec extends RetryOnFailureSubclassSpec {

    static overrideCount = 0

    void 'inherits retryCount'() {
        when:
        if (retryCount < 9) {
            retryCount++
            throw new RuntimeException('not there yet')
        }

        then:
        retryCount == 9
    }

    @RetryOnFailure(times=20)
    void 'feature override'() {
        when:
        if (overrideCount < 20) {
            overrideCount++
            throw new Exception('not yet')
        }

        then:
        overrideCount == 20
    }

}
