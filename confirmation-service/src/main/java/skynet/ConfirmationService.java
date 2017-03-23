package skynet;

import com.github.kristofa.brave.LocalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Random;

@SpringBootApplication
@EnableJpaRepositories
public class ConfirmationService {
    public static void main(String[] args) {
		SpringApplication.run(ConfirmationService.class, args);
	}

	@Configuration
	public static class JpaBraveConfiguration extends HibernateJpaAutoConfiguration {

		public JpaBraveConfiguration(DataSource dataSource, JpaProperties jpaProperties, ObjectProvider<JtaTransactionManager> jtaTransactionManager, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			super(dataSource, jpaProperties, jtaTransactionManager, transactionManagerCustomizers);
		}

		@Autowired
		HibernateBraveInterceptor hibernateBraveInterceptor;

		@Override
		public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder) {
			final LocalContainerEntityManagerFactoryBean factory = super.entityManagerFactory(factoryBuilder);
			factory.getJpaPropertyMap().put("hibernate.ejb.interceptor", hibernateBraveInterceptor);
			return factory;
		}

    	@Bean
		HibernateBraveInterceptor hibernateBraveInterceptor(LocalTracer localTracer) {
    		return new HibernateBraveInterceptor(localTracer);
		}
	}

	@RestController
	public static class ConfirmationApi {
		private final Logger log = LoggerFactory.getLogger(getClass());

		private final AnnihilationRequestRepository requestRepository;

		@Autowired
		public ConfirmationApi(AnnihilationRequestRepository requestRepository) {
			this.requestRepository = requestRepository;
		}

		@GetMapping("/confirm")
		public String confirm(@RequestParam("target") String target) {
		    log.info(target);
		    requestRepository.save(new AnnihilationRequest(target, LocalDateTime.now()));
		    return new Random().nextInt(10) > 3 ? "CONFIRMED " + LocalDateTime.now() : "DENIED";
		}

	}
}