package org.javaunit.autoparams.generator;

import java.lang.reflect.Type;
import org.javaunit.autoparams.customization.Customizer;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class ObjectGenerationContext {

    private final ExtensionContext extensionContext;
    private ObjectGenerator generator;

    public ObjectGenerationContext(
        ExtensionContext extensionContext, ObjectGenerator generator) {
        this.extensionContext = extensionContext;
        this.generator = generator;
    }

    @SuppressWarnings("unchecked")
    public <T> T generate(Class<T> type) {
        return (T) generate(() -> type);
    }

    public Object generate(ObjectQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("The argument 'query' is null.");
        }

        final Type type = query.getType();
        if (ExtensionContext.class.equals(type)) {
            return extensionContext;
        } else if (ObjectGenerator.class.equals(type)) {
            return generator;
        } else {
            return generateObject(query);
        }
    }

    private Object generateObject(ObjectQuery query) {
        try {
            return generator.generate(query, this).unwrapOrElseThrow();
        } catch (UnwrapFailedException exception) {
            throw composeGenerationFailedException(query, exception);
        }
    }

    private RuntimeException composeGenerationFailedException(ObjectQuery query, Throwable cause) {
        String messageFormat = "Object cannot be created with the given query '%s'."
            + " This can happen if the query represents an interface or abstract class.";
        String message = String.format(messageFormat, query);
        return new RuntimeException(message, cause);
    }

    public void customizeGenerator(Customizer customizer) {
        if (customizer == null) {
            throw new IllegalArgumentException("The argument 'customizer' is null.");
        }

        generator = customizer.customize(generator);
    }

}
