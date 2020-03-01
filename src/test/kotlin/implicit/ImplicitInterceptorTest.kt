package implicit

import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver
import com.fasterxml.jackson.databind.module.SimpleModule
import org.junit.jupiter.api.Assertions
import java.util.*


class ImplicitInterceptorTest {

    // @Test
    fun test() {
        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier1 = factory.getSupplier(Outer::class.java)
        val supplier2 = factory.getSupplier(Outer.Inner::class.java)

        val pojo = supplier1.get()
        pojo.setPartitionKey(UUID.randomUUID().toString())

        val data = supplier2.get()
        data.setQualifier("test")
        pojo.setData(data)

        Assertions.assertNotNull(pojo.getPartitionKey())
        Assertions.assertNotNull(pojo.getData())

        val mapper = ObjectMapper()

        val module = SimpleModule("CustomModel")

        val resolver = SimpleAbstractTypeResolver()
        resolver.addMapping(Outer.Inner::class.java, data::class.java)

        module.setAbstractTypes(resolver)

        mapper.registerModule(module)

        println(mapper.writeValueAsString(pojo))
    }

    // @Test
    fun unmarshal() {
        val json = "{\"data\":{\"qualifier\":\"test\"},\"partitionKey\":\"8b4a30e0-d2ec-4db5-90a3-41d5615a0e2a\"}"

        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }
        val supplier1 = factory.getSupplier(Outer::class.java, false)

        val mapper = ObjectMapper()

        val module = SimpleModule("CustomModel")

        val resolver = ImplicitTypeResolver()
        module.setAbstractTypes(resolver)
        mapper.registerModule(module)

        val data = mapper.readValue(json, supplier1.get()::class.java)
        println(data.getPartitionKey())
        println(data.getData().getQualifier())
    }

    interface Outer {
        fun getPartitionKey(): String
        fun setPartitionKey(key: String)

        fun getData(): Inner
        fun setData(data: Inner)

        interface Inner {
            fun getQualifier(): String
            fun setQualifier(key: String)
        }
    }

    class ImplicitTypeResolver : SimpleAbstractTypeResolver() {

        val factory = Implicit { "${this.javaClass.name.toLowerCase()}.${it.simpleName}" }

        override fun findTypeMapping(config: DeserializationConfig?, type: JavaType): JavaType {
            val mapping = super.findTypeMapping(config, type)

            if (mapping == null) {
                val supplier2 = factory.getSupplier(type.rawClass, false)
                val cls1: Class<out Any> = type.rawClass as Class<Any>
                val cls2: Class<out Any> = supplier2.get()::class.java
                // addMapping(cls1, cls2)
            }

            return super.findTypeMapping(config, type)
        }
    }

}
