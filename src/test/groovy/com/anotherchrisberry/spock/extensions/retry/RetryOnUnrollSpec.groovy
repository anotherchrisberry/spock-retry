package com.anotherchrisberry.spock.extensions.retry

import spock.lang.Specification
import spock.lang.Unroll

class RetryOnUnrollSpec extends Specification {

  static Integer attempts = 0

  @Unroll
  @RetryOnFailure(times=3)
  void 'unrolling'() {
    when:
    if (attempts < 3) {
      attempts++
      throw new RuntimeException("have not tried enough times ($attempts)")
    }
    attempts = 0

    then:
    true

    where:
    run << [1, 2, 3]
  }
}
