package breakable.toy.breakable_toy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BreakableToyApplicationTests {

	@Autowired
	private TestRestTemplate testRest;

	@Value("${local.server.port}")
	private int port;

	@Test
	void testSayHello() {
		String url = "http://localhost:" + port + "/product/hello";
		String response = testRest.getForObject(url, String.class);
		assertThat(response).isEqualTo("Hello World");
	}

}
