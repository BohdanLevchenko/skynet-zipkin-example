package skynet;

import com.github.kristofa.brave.LocalTracer;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

public class HibernateBraveInterceptor extends EmptyInterceptor {

	private final LocalTracer localTracer;

	public HibernateBraveInterceptor(LocalTracer localTracer) {
		this.localTracer = localTracer;
	}

	@Override
	public void afterTransactionBegin(Transaction tx) {
		localTracer.startNewSpan("Hibernate", "Tx");
		super.afterTransactionBegin(tx);
	}

	@Override
	public void afterTransactionCompletion(Transaction tx) {
		localTracer.finishSpan();
		super.afterTransactionCompletion(tx);
	}

	@Override
	public String onPrepareStatement(String sql) {
		localTracer.submitAnnotation(sql);
		return super.onPrepareStatement(sql);
	}
}
