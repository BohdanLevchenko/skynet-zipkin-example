package skynet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan
public class AnnihilationService {
    public static void main(String[] args) {
		SpringApplication.run(AnnihilationService.class, args);
	}

	@RestController
	public static class AnnihilationApi {
		private final Logger log = LoggerFactory.getLogger(getClass());
		private final IAnnihilationService annihilationService;


		@Autowired
		public AnnihilationApi(IAnnihilationService annihilationService) {
			this.annihilationService = annihilationService;
		}

		@PostMapping("/annihilate")
		public String annihilate(@RequestParam("target") String target) {
			log.info(target);
			return "ANNIHILATION [" + annihilationService.confirm(target) + "]";
		}

	}

	@Bean
	TracedBeanEnhancer tracedBeanEnhancer() {
    	return new TracedBeanEnhancer();
	}


	interface IAnnihilationService {
    	String confirm(String target);
	}

	@Component
	public static class TraceableAnnihilationService implements IAnnihilationService {

		private final RestTemplate restTemplate;

		@Autowired
		public TraceableAnnihilationService(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}

		@Override
		@Traced("annihilate")
		public String confirm(String target) {
			return restTemplate.getForObject("http://localhost:8283/confirmation-service/confirm?target={target}", String.class, target);
		}
	}
}
