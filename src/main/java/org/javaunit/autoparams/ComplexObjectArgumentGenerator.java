package org.javaunit.autoparams;

import static java.util.Arrays.stream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

final class ComplexObjectArgumentGenerator implements ArgumentGenerator {

    @Override
    public Optional<Object> generate(Parameter parameter, ArgumentGenerationContext context) {
        return resolveConstructor(parameter.getType()).map(c -> createInstance(c, context)).map(Optional::of)
                .orElse(Optional.empty());
    }

    private Optional<Constructor<?>> resolveConstructor(Class<?> type) {
        return isSimpleType(type) ? Optional.empty() : stream(type.getConstructors()).findFirst();
    }

    private boolean isSimpleType(Class<?> type) {
        return type.equals(Boolean.class) || type.equals(Integer.class) || type.equals(Float.class)
                || type.equals(Double.class) || type.equals(String.class) || type.equals(BigDecimal.class)
                || type.equals(UUID.class);
    }

    private Object createInstance(Constructor<?> constructor, ArgumentGenerationContext context) {
        try {
            return constructor.newInstance(resolveArguments(constructor.getParameters(), context));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] resolveArguments(Parameter[] parameters, ArgumentGenerationContext context) {
        ArgumentGenerator generator = context.getGenerator();
        return stream(parameters).map(p -> generator.generate(p, context)).map(a -> a.orElse(null)).toArray();
    }

}
