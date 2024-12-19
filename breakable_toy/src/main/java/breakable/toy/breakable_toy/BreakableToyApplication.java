package breakable.toy.breakable_toy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "main.java.breakable.toy.breakable_toy")
public class BreakableToyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreakableToyApplication.class, args);
	}

}
