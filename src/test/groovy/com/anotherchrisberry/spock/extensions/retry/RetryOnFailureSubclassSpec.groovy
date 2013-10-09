package com.anotherchrisberry.spock.extensions.retry

class RetryOnFailureSubclassSpec extends RetryOnFailureSuperclassSpec {

    void 'inherits retryCount'() {
        when:
        if (retryCount < 9) {
            retryCount++
            throw new RuntimeException('not there yet')
        }

        then:
        retryCount == 9
    }

}
