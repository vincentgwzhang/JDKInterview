package jdk16;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Record 面试核心：
 *
 * 1. JDK 16 中 record 转正。JDK 14/15 是 preview。
 *
 * 2. record 适合表达“透明的数据载体”：字段少、主要承载数据、相等性按数据内容判断。
 *    编译器会自动生成：
 *    - private final fields
 *    - canonical constructor
 *    - accessor methods，例如 name()，不是 getName()
 *    - equals/hashCode/toString
 *
 * 3. record 的直接父类固定是 java.lang.Record。
 *    所以 record 不能 extends 其他 class，也不能被继承；record 是隐式 final。
 *    但 record 可以 implements interface。
 *
 * 4. record 不是 JavaBean。
 *    它没有默认无参构造器；访问器叫 componentName()，不是 getComponentName()。
 *
 * 5. record 是“浅不可变”。
 *    component 引用本身是 final，不能重新指向别的对象；
 *    但如果 component 指向的是可变对象，例如 List，这个 List 内部仍然可能被修改。
 *
 * 6. compact constructor 可以校验和规范化入参。
 *    注意：compact constructor 里不要给字段赋值；只处理参数，最后由编译器赋给字段。
 */
class RecordsInterviewTest {

    interface Named {
        String name();

        String anotherMethod();

        static String clean(String s) {
            return s.strip();
        }
    }

    record User(String name, int age) implements Named {
        User {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            if (age < 0) {
                throw new IllegalArgumentException("age must not be negative");
            }
            name = Named.clean(name);

        }

        @Override
        public String anotherMethod() {
            throw new UnsupportedOperationException("Unimplemented method 'anotherMethod'");
        }
    }

    record Team(String name, List<String> members) {
    }

    record SafeTeam(String name, List<String> members) {
        SafeTeam {
            members = List.copyOf(members);
        }
    }

    /*
     * 自动生成方法面试点：
     *
     * 1. accessor 是 name() / age()，不是 getName() / getAge()。
     * 2. equals/hashCode 按所有 record components 比较。
     * 3. toString 默认包含 record 名称和 component 名称，适合调试。
     */
    @Test
    void recordGeneratesAccessorsEqualsHashCodeAndToString() {
        User u1 = new User(" Alice ", 30);
        User u2 = new User("Alice", 30);

        assertEquals("Alice", u1.name());
        assertEquals(30, u1.age());
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertEquals("User[name=Alice, age=30]", u1.toString());
    }

    /*
     * compact constructor 面试点：
     *
     * 1. compact constructor 没有参数列表，参数名直接来自 record header。
     * 2. 可以在里面校验参数、改写参数。
     * 3. 如果抛异常，对象不会创建成功。
     */
    @Test
    void compactConstructorCanValidateAndNormalizeArguments() {
        assertEquals("Bob", new User(" Bob ", 20).name());
        assertThrows(IllegalArgumentException.class, () -> new User("", 20));
        assertThrows(IllegalArgumentException.class, () -> new User("Bob", -1));
    }

    /*
     * implements interface 面试点：
     *
     * record 不能继承其他 class，但可以实现接口。
     * 这个例子里 User 自动生成的 name() 刚好满足 Named.name()。
     */
    @Test
    void recordCanImplementInterface() {
        Named named = new User("Carol", 25);

        assertEquals("Carol", named.name());
    }

    /*
     * 浅不可变面试坑：
     *
     * record component 是 final，只能保证 members 这个引用不能在 record 内被重新赋值。
     * 但如果传入的是可变 List，外部继续修改这个 List，record 观察到的内容也会变化。
     */
    @Test
    void recordIsOnlyShallowlyImmutable() {
        List<String> members = new ArrayList<>(List.of("a"));
        Team team = new Team("dev", members);

        members.add("b");

        assertEquals(List.of("a", "b"), team.members());
    }

    /*
     * 防御性拷贝面试点：
     *
     * 如果 record 持有集合、数组、Date 等可变对象，通常要在构造器里做防御性拷贝。
     * List.copyOf 会创建不可变副本，并拒绝 null 元素。
     */
    @Test
    void defensiveCopyProtectsRecordFromMutableComponents() {
        List<String> members = new ArrayList<>(List.of("a"));
        SafeTeam team = new SafeTeam("dev", members);

        members.add("b");

        assertEquals(List.of("a"), team.members());
        assertThrows(UnsupportedOperationException.class, () -> team.members().add("c"));
    }
}
