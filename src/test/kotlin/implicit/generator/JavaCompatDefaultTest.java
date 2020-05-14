package implicit.generator;

import implicit.Implicit;
import implicit.annotation.generator.Default;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JavaCompatDefaultTest {

    @Test
    public void test() {
        final Implicit factory = new Implicit(e -> "implicit.generator.compat." + e.getSimpleName());
        final Supplier<? extends Entity> supplier = factory.getSupplier(Entity.class);

        final Entity entity = supplier.get();
        Assertions.assertEquals("defaultValue", entity.getStringVal());
        Assertions.assertEquals(0, entity.getIntVal());
        Assertions.assertEquals(1.5f, entity.getFloatVal());
        Assertions.assertEquals(1.5, entity.getDoubleVal());
        Assertions.assertEquals(true, entity.getBooleanVal());
        Assertions.assertEquals((byte) 0, entity.getByteVal());
        Assertions.assertEquals((short) 0, entity.getShortVal());
        Assertions.assertEquals(0, entity.getLongVal());
        Assertions.assertNotNull(entity.getListVal());
        Assertions.assertNotNull(entity.getMapVal());
    }

    public interface Entity {
        @Default("defaultValue")
        String getStringVal();
        @Default("0")
        Integer getIntVal();
        @Default("1.5")
        Float getFloatVal();
        @Default("1.5")
        Double getDoubleVal();
        @Default("true")
        Boolean getBooleanVal();
        @Default("0")
        Short getShortVal();
        @Default("0")
        Long getLongVal();
        @Default("0")
        Byte getByteVal();
        @Default
        List<String> getListVal();
        @Default
        Map<String, String> getMapVal();
    }

}
