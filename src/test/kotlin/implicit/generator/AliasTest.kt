package implicit.generator

import implicit.Implicit
import implicit.annotation.Implicit.Type.GENERATOR
import implicit.annotation.generator.Alias
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class AliasTest {

    @Test
    fun `create instance with alias annotation`() {
        val factory = Implicit { "implicit.generator.alias.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java)

        val pojo = supplier.get()
        pojo.setId(UUID.randomUUID().toString())

        Assertions.assertNotNull(pojo.getPartitionKey())
    }

    @Test
    fun `create instance with alias annotation 2`() {
        val factory = Implicit { "implicit.generator.alias.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java)

        val pojo = supplier.get()
        pojo.setPartitionKey(UUID.randomUUID().toString())

        Assertions.assertNotNull(pojo.getPartitionKey())
    }

    @Test
    fun `create instance with alias annotation 3`() {
        val factory = Implicit { "implicit.generator.alias.${it.simpleName}" }
        val supplier = factory.getSupplier(Entity::class.java)

        val pojo = supplier.get()
        pojo.setPartitionKey(UUID.randomUUID().toString())

        Assertions.assertNotNull(pojo.getId())
    }

    @Test
    fun `create instance with alias annotation 4`() {
        val factory = Implicit { "implicit.generator.alias.${it.simpleName}" }
        val pojo = factory.instantiate(Entity::class.java, mapOf("partitionKey" to UUID.randomUUID().toString()))

        Assertions.assertNotNull(pojo.getId())
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
        fun setPartitionKey(partitionKey: String)
    }

}
