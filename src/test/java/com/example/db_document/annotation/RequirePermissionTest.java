package com.example.db_document.annotation;

import com.example.db_document.pojo.PermissionType;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static org.junit.jupiter.api.Assertions.*;

class RequirePermissionTest {

    @Test
    void annotationMetadata_isCorrect() {
        Retention retention = RequirePermission.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());

        Target target = RequirePermission.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new java.lang.annotation.ElementType[]{METHOD}, target.value());
    }

    @Test
    void defaultValue_isEditor() throws Exception {
        class Dummy {
            @RequirePermission
            void m() {}
        }

        RequirePermission annotation = Dummy.class.getDeclaredMethod("m").getAnnotation(RequirePermission.class);
        assertNotNull(annotation);
        assertEquals(PermissionType.EDITOR, annotation.value());
    }
}

