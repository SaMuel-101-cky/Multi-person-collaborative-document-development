package com.example.db_document.annotation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static org.junit.jupiter.api.Assertions.*;

class LogTest {

    @Test
    void annotationMetadata_isCorrect() {
        Retention retention = Log.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());

        Target target = Log.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new java.lang.annotation.ElementType[]{METHOD}, target.value());

        assertNotNull(Log.class.getAnnotation(Documented.class));
    }
}

