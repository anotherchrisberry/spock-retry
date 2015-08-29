package com.anotherchrisberry.spock.extensions.retry

class RetryOnFailureCleanupSubclassSpec extends RetryOnFailureCleanupSpec {

  void cleanupSpec() {
    cleanupCalls = 0
  }

  void 'should call parent setup method'() {
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
