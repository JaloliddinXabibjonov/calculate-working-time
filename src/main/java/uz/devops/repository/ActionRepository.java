package uz.devops.repository;

import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.devops.domain.Action;
import uz.devops.domain.enumeration.Status;

/**
 * Spring Data SQL repository for the Action entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionRepository extends JpaRepository<Action, Long>, JpaSpecificationExecutor<Action> {
    Set<Action> getActionsByStatusOrStatus(Status status, Status status1);
    Set<Action> getActionsByStatus(Status status);
    Set<Action> getActionsByStatusOrStatusOrStatus(@NotNull Status status, @NotNull Status status2, @NotNull Status status3);

    @Query(value = "select a.command from action a where a.name=?1", nativeQuery = true)
    String getCommandByName(@Size(max = 25) String name);
}
