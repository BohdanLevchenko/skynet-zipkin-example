package skynet;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.LocalTracer;
import com.github.kristofa.brave.Sampler;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.spring.BraveClientHttpRequestInterceptor;
import com.github.kristofa.brave.spring.ServletHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

@Configuration
@Import({BraveClientHttpRequestInterceptor.class, ServletHandlerInterceptor.class})
public class TraceConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	ServletHandlerInterceptor servletHandlerInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(servletHandlerInterceptor);
		super.addInterceptors(registry);
	}

	@Bean
	SpanNameProvider spanNameProvider() {
		return new DefaultSpanNameProvider();
	}

	@Bean
	LocalTracer localTracer(Brave brave) {
		return brave.localTracer();
	}

	@Bean
	Brave brave(@Value("${spring.application.name}") String name) {
		return new Brave.Builder(name)
				.reporter(reporter())
				.traceSampler(sampler())
				.build();
	}

	@Bean
	public Sampler sampler() {
		return Sampler.create(0.1f);
	}

	@Bean
	public Reporter<Span> reporter() {
		return AsyncReporter.builder(sender()).build();
	}

	@Bean
	public Sender sender() {
		return OkHttpSender.create("http://localhost:9411/api/v1/spans");
	}

	@Bean
	RestTemplate restTemplate(ClientHttpRequestInterceptor traceInterceptor) {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(traceInterceptor);
		return restTemplate;
	}

}
