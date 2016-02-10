package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

class RetryOnFailureBeforeRetryMethod extends Specification{

    static Integer classLevelTries = 0
    static Integer onRetry = 0

    @RetryOnFailure(times=3)
    void 'expect beforeRetry method is called once'() {

        when:
        if (classLevelTries < 1) {
            classLevelTries ++
            throw new RuntimeException('no problem')
        }

        then:
        onRetry == 1
    }

    void beforeRetry(){
        onRetry ++
    }
}
