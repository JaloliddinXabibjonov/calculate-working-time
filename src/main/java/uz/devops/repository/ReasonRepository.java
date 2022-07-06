package uz.devops.repository;

import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Reason;
import uz.devops.domain.enumeration.Status;

/**
 * Spring Data SQL repository for the Reason entity.
 */
@Repository
public interface ReasonRepository extends JpaRepository<Reason, Long>, JpaSpecificationExecutor<Reason> {
    //    @Query(value = "select r.name from reason r where r.status=?1",nativeQuery = true)
    Set<Reason> getAllByStatus(@NotNull Status status);

    Reason getByName(@NotNull String name);
}
