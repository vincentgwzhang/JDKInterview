package jdk17;

import org.junit.jupiter.api.Test;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static org.junit.jupiter.api.Assertions.*;

class RandomGeneratorTest {

    @Test
    void randomGeneratorFactory() {
        RandomGenerator rng = RandomGeneratorFactory
                .<RandomGenerator>of("Xoshiro256PlusPlus")
                .create();
        int value = rng.nextInt(1, 100);
        assertTrue(value >= 1 && value < 100);
    }
}
