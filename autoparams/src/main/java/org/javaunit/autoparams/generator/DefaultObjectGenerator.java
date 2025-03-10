package org.javaunit.autoparams.generator;

import org.javaunit.autoparams.customization.RecursionGuard;

final class DefaultObjectGenerator extends CompositeObjectGenerator {

    public DefaultObjectGenerator() {
        super(
            new RecursionGuard().customize(
                new CompositeObjectGenerator(
                    new ObjectGenerationContextGenerator(),
                    new PrimitiveValueGenerator(),
                    new SimpleValueObjectGenerator(),
                    new CollectionGenerator(),
                    new StreamGenerator(),
                    new ComplexObjectGenerator()))
        );
    }

}
