package collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparableComparatorTest {

    record Student(String name, int score) implements Comparable<Student> {
        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.score, other.score);
        }
    }

    @Test
    void comparableNaturalOrder() {
        List<Student> list = new ArrayList<>(List.of(
                new Student("Bob", 85),
                new Student("Alice", 90),
                new Student("Cara", 80)
        ));
        list.sort(null);
        assertEquals("Cara", list.get(0).name());
        assertEquals("Alice", list.get(2).name());
    }

    @Test
    void comparatorChaining() {
        List<Student> list = new ArrayList<>(List.of(
                new Student("Bob", 90),
                new Student("Alice", 90),
                new Student("Cara", 85)
        ));
        list.sort(Comparator
                .comparingInt(Student::score).reversed()
                .thenComparing(Student::name));
        assertEquals("Alice", list.get(0).name());
        assertEquals("Bob", list.get(1).name());
        assertEquals("Cara", list.get(2).name());
    }
}
