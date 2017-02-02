package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Ignore
import spock.lang.Specification

@RetryOnFailure
class RetryOnFailureSetupSingleSpec extends Specification {

    void setup() {
        throw new RuntimeException("setup failed")
    }

    @Ignore
    void 'should fail when setup fails once'() {
        given:
        throw new Exception("spec itself failed")
    }
}
