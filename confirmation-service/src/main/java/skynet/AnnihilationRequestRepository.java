package skynet;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnihilationRequestRepository extends CrudRepository<AnnihilationRequest, Long> {
}
