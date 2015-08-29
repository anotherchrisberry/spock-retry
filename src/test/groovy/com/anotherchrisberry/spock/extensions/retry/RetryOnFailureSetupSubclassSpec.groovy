package com.anotherchrisberry.spock.extensions.retry

@RetryOnFailure(times=5)
class RetryOnFailureSetupSubclassSpec extends RetryOnFailureSetupSpec {

  static Integer specCalls = 0

  void setupSpec() {
    setupCalls = 0
  }

  void 'should call parent setup method'() {
    given:
    Mockable mockable = Mock()

    when:
    mockable.doMockyThing()

    then:
    setupCalls == 2
    1 * mockable.doMockyThing()
    0 * _
  }

  void 'should still try to run own method when setup fails in superclass'() {
    when:
    if (specCalls < 3) {
      specCalls++
      throw new RuntimeException("have not tried enough times ($specCalls)")
    }

    then:
    setupCalls == 2
    specCalls == 3
  }
}
