package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.compiler.model.FeatureMethod
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo

class RetrySpecExtension extends AbstractAnnotationDrivenExtension<RetryOnFailure> {

    void visitFeatureAnnotation(RetryOnFailure retries, FeatureInfo feature) {
        clearInterceptors(feature)
        feature.getFeatureMethod().interceptors.add(new RetryInterceptor(getNumberOfRetries(retries)))
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
                addInterceptors(feature, retries)
            }
        }
    }

    int getNumberOfRetries(RetryOnFailure retries) {
        String defaultRetries = Integer.toString(retries.times())
        return Integer.parseInt(System.getProperty("spock-retry.times", defaultRetries))
    }

    private List<MethodInfo> getInterceptableMethods(FeatureInfo feature) {
        SpecInfo spec = feature.getSpec()
        [ spec.setupMethods,
          spec.setupSpecMethods,
          spec.cleanupMethods,
          spec.cleanupSpecMethods,
          feature.featureMethod
        ].flatten().unique() as List<MethodInfo>
    }

    private void clearInterceptors(FeatureInfo featureInfo) {
        List<MethodInfo> interceptableMethods = getInterceptableMethods(featureInfo)
        interceptableMethods.each { it.interceptors.removeAll { it.class == RetryInterceptor } }
    }

    private void addInterceptors(FeatureInfo featureInfo, RetryOnFailure retries) {
        def interceptor = new RetryInterceptor(getNumberOfRetries(retries))
        getInterceptableMethods(featureInfo).each {
            it.addInterceptor(interceptor)
        }
    }
}
