package implicit.generator

import implicit.Implicit
import implicit.annotation.Implicit.Type.GENERATOR
import implicit.annotation.generator.Alias
import implicit.annotation.generator.Mixin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MixinTest {

    @Test
    fun `create instance with getter annotation`() {
        val factory = Implicit { "implicit.generator.mixin.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java)

        val pojo = supplier.get()
        pojo.setId(UUID.randomUUID().toString())

        Assertions.assertNotNull(pojo.getPartitionKey())
        Assertions.assertEquals(pojo::class.java.interfaces.size, 3)
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    @Alias(value = "partitionKey")
    @Mixin(PartitionKeyAware::class)
    @implicit.annotation.Implicit(GENERATOR)
    annotation class PartitionKey

    interface Entity : PartitionKeyAware {
        @PartitionKey
        fun getId():String
        fun setId(id:String)
    }

    interface PartitionKeyAware {
        fun getPartitionKey():String
    }

}
