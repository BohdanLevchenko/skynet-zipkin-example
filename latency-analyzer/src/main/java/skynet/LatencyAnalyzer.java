package skynet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import zipkin.autoconfigure.ui.ZipkinUiAutoConfiguration;
import zipkin.server.EnableZipkinServer;

@SpringBootApplication
@EnableZipkinServer
@Import(ZipkinUiAutoConfiguration.class)
public class LatencyAnalyzer {
    public static void main(String[] args) {
		SpringApplication.run(LatencyAnalyzer.class, args);
	}
}
