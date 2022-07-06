package uz.devops.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link uz.devops.domain.Worker} entity. This class is used
 * in {@link uz.devops.web.rest.WorkerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /workers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class WorkerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private LongFilter workerTgId;

    private StringFilter role;

    private LongFilter workHistoryId;

    private Boolean distinct;

    public WorkerCriteria() {}

    public WorkerCriteria(WorkerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.workerTgId = other.workerTgId == null ? null : other.workerTgId.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.workHistoryId = other.workHistoryId == null ? null : other.workHistoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WorkerCriteria copy() {
        return new WorkerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFullName() {
        return fullName;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public LongFilter getWorkerTgId() {
        return workerTgId;
    }

    public LongFilter workerTgId() {
        if (workerTgId == null) {
            workerTgId = new LongFilter();
        }
        return workerTgId;
    }

    public void setWorkerTgId(LongFilter workerTgId) {
        this.workerTgId = workerTgId;
    }

    public StringFilter getRole() {
        return role;
    }

    public StringFilter role() {
        if (role == null) {
            role = new StringFilter();
        }
        return role;
    }

    public void setRole(StringFilter role) {
        this.role = role;
    }

    public LongFilter getWorkHistoryId() {
        return workHistoryId;
    }

    public LongFilter workHistoryId() {
        if (workHistoryId == null) {
            workHistoryId = new LongFilter();
        }
        return workHistoryId;
    }

    public void setWorkHistoryId(LongFilter workHistoryId) {
        this.workHistoryId = workHistoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkerCriteria that = (WorkerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(workerTgId, that.workerTgId) &&
            Objects.equals(role, that.role) &&
            Objects.equals(workHistoryId, that.workHistoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, workerTgId, role, workHistoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fullName != null ? "fullName=" + fullName + ", " : "") +
            (workerTgId != null ? "workerTgId=" + workerTgId + ", " : "") +
            (role != null ? "role=" + role + ", " : "") +
            (workHistoryId != null ? "workHistoryId=" + workHistoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
