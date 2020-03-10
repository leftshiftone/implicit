package implicit.generator

import implicit.Implicit
import implicit.annotation.Implicit.Type.GENERATOR
import implicit.annotation.generator.Alias
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class AliasTest {

    @Test
    fun `create instance with getter annotation`() {
        val factory = Implicit { "implicit.generator.alias.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java)

        val pojo = supplier.get()
        pojo.setId(UUID.randomUUID().toString())

        Assertions.assertNotNull(pojo.getPartitionKey())
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    @Alias(value = "partitionKey")
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
