package com.kellen.database;

import com.kellen.entity.EntityBase;
import com.kellen.entity.TenantEntityBase;
import com.kellen.message.entity.UserMessage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/** 用户消息实体直接继承租户基类，基类已有的公共字段（含 id、version、code、description、label、sort）不重复声明。 */
class EntityBaseFieldsTest {

    @Test
    void userMessageExtendsTenantBaseWithoutRedeclaringBaseFields() {
        Set<String> base = Stream.of(EntityBase.class, TenantEntityBase.class)
                .flatMap(type -> Arrays.stream(type.getDeclaredFields()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .collect(Collectors.toSet());
        assertThat(UserMessage.class.getSuperclass()).isEqualTo(TenantEntityBase.class);
        assertThat(Arrays.stream(UserMessage.class.getDeclaredFields()).map(Field::getName)).doesNotContainAnyElementsOf(base);
    }
}
