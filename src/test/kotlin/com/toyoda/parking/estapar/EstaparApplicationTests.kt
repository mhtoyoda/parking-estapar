package com.toyoda.parking.estapar

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@Disabled("Requer MySQL e o simulador em localhost:9000 — teste de integração, não unitário")
@SpringBootTest
class EstaparApplicationTests {

	@Test
	fun contextLoads() {
	}

}
