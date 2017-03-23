package skynet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class AnnihilationRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private String target;

	private LocalDateTime timestamp;

	protected AnnihilationRequest() {
	}

	public AnnihilationRequest(String target, LocalDateTime timestamp) {
		this.target = target;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public String getTarget() {
		return target;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
