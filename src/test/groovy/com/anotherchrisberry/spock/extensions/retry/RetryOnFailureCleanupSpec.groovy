package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification

@RetryOnFailure(times=2)
class RetryOnFailureCleanupSpec extends Specification {

  static Integer cleanupCalls = 0

  void cleanup() {
    if (cleanupCalls < 2) {
      cleanupCalls++
      throw new RuntimeException("have not tried enough times ($cleanupCalls)")
    }
  }

  void 'class level test'() {
    given:
    Mockable mockable = Mock()

    when:
    mockable.doMockyThing()

    then:
    cleanupCalls == 2
    1 * mockable.doMockyThing()
    0 * _
  }
}
