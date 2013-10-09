# Spock Extensions

Really just one: `@RetryOnFailure`, which allows your tests to be a little more resilient to transient failures.

## Why
If you've ever written tests that run in the browser (Geb, Selenium, etc.), or depend on external systems where response times aren't guaranteed, you may encounter transient errors that are difficult to track down.

There's a valid argument that if your tests are failing in a transient way, they're too brittle and should be rewritten. This is often a problem of unknown complexity - you might figure it out in five minutes or you might spend five days trying to find it and not have any luck. At that point, you can ignore it, live with the failing test, or throw this annotation on it.

## Usage

The annotation can be applied to a particular feature:
```groovy
class ExampleSpec extends Specification {
	
	@RetryOnFailure
	void 'does something'() {
		// will try to run twice before failing
	}
}
```

It can be applied to a particular Specification:

```groovy
@RetryOnFailure(times=3)
class ExampleSpec extends Specification {
	
	void 'does something'() {
		// will try to run three times before failing
	}
}
```

It can be applied to a super class:
```groovy

@RetryOnFailure(times=4)
class BaseSpec extends Specification {
	
}

class ChildSpec extends BaseSpec {
	
	void 'do something flaky'() {
		// will try to run four times before failing
	}
}
```

The `times` argument is optional; with none specified, the runner will attempt the feature twice before failing. Annotations of greater specificity *should* override annotations of lesser specificity, i.e. subclass annotations override superclass annotations, feature (i.e. method) annotations override class annotations. I say *should* because it's based on observed behavior, not a deep dive into the Spock internals to understand the order in which the annotations are visited.