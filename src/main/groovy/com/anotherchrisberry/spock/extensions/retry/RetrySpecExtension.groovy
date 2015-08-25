package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

class RetrySpecExtension extends AbstractAnnotationDrivenExtension<RetryOnFailure> {

    void visitFeatureAnnotation(RetryOnFailure retries, FeatureInfo feature) {
        clearInterceptors(feature)
        feature.getFeatureMethod().interceptors.add(new RetryInterceptor(retries.times()))
    }

    void visitSpecAnnotation(RetryOnFailure retries, SpecInfo spec) {

        SpecInfo specToAdd = spec
        spec.subSpec
        List<SpecInfo> selfAndSuperSpecs = [spec]
        List<SpecInfo> selfAndSubSpecs = [spec]
        while (specToAdd.getSuperSpec()) {
            selfAndSuperSpecs << specToAdd.getSuperSpec()
            specToAdd = specToAdd.getSuperSpec()
        }
        specToAdd = spec
        while (specToAdd.subSpec) {
            selfAndSubSpecs << specToAdd.subSpec
            specToAdd = specToAdd.subSpec
        }

        if (selfAndSuperSpecs.any { it.getReflection().isAnnotationPresent(RetryOnFailure.class)}) {
            List<FeatureInfo> featuresToRetry = [selfAndSubSpecs.features].flatten().unique()
            for (FeatureInfo feature : featuresToRetry) {
                clearInterceptors(feature)
                String defaultRetries = Integer.toString(retries.times())
                int numberOfRetries = Integer.parseInt(System.getProperty("spock-retry.times", defaultRetries))
                feature.getFeatureMethod().addInterceptor(new RetryInterceptor(numberOfRetries))
            }
        }
    }

    private void clearInterceptors(FeatureInfo featureInfo) {
        featureInfo.featureMethod.interceptors.removeAll { it.class == RetryInterceptor }
    }
}
