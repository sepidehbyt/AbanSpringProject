package com.example.demo.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpringClassScanner {

    Map<String, String> response = new HashMap<>();

    public Map<String, String> findAnnotatedClasses(String scanPackage) {

        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage)) {
            printMetadata(beanDef);
        }

        return response;

    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Sepideh.class));
        return provider;
    }

    private void printMetadata(BeanDefinition beanDef) {
        try {
            Class<?> cl = Class.forName(beanDef.getBeanClassName());
            Sepideh findable = cl.getAnnotation(Sepideh.class);
            response.put(cl.getSimpleName().toLowerCase(), findable.value().toLowerCase());
        } catch (Exception e) {
            System.err.println("Got exception: " + e.getMessage());
        }

    }
}