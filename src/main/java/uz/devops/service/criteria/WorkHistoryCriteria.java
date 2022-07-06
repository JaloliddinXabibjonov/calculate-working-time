package uz.devops.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import uz.devops.domain.enumeration.Status;

/**
 * Criteria class for the {@link uz.devops.domain.WorkHistory} entity. This class is used
 * in {@link uz.devops.web.rest.WorkHistoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /work-histories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class WorkHistoryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter start;

    private InstantFilter toLunch;

    private InstantFilter fromLunch;

    private InstantFilter end;

    private StringFilter reasonDescription;

    private StatusFilter status;

    private LongFilter workerId;

    private LongFilter reasonId;

    private Boolean distinct;

    public WorkHistoryCriteria() {}

    public WorkHistoryCriteria(WorkHistoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.start = other.start == null ? null : other.start.copy();
        this.toLunch = other.toLunch == null ? null : other.toLunch.copy();
        this.fromLunch = other.fromLunch == null ? null : other.fromLunch.copy();
        this.end = other.end == null ? null : other.end.copy();
        this.reasonDescription = other.reasonDescription == null ? null : other.reasonDescription.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.workerId = other.workerId == null ? null : other.workerId.copy();
        this.reasonId = other.reasonId == null ? null : other.reasonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WorkHistoryCriteria copy() {
        return new WorkHistoryCriteria(this);
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

    public InstantFilter getStart() {
        return start;
    }

    public InstantFilter start() {
        if (start == null) {
            start = new InstantFilter();
        }
        return start;
    }

    public void setStart(InstantFilter start) {
        this.start = start;
    }

    public InstantFilter getToLunch() {
        return toLunch;
    }

    public InstantFilter toLunch() {
        if (toLunch == null) {
            toLunch = new InstantFilter();
        }
        return toLunch;
    }

    public void setToLunch(InstantFilter toLunch) {
        this.toLunch = toLunch;
    }

    public InstantFilter getFromLunch() {
        return fromLunch;
    }

    public InstantFilter fromLunch() {
        if (fromLunch == null) {
            fromLunch = new InstantFilter();
        }
        return fromLunch;
    }

    public void setFromLunch(InstantFilter fromLunch) {
        this.fromLunch = fromLunch;
    }

    public InstantFilter getEnd() {
        return end;
    }

    public InstantFilter end() {
        if (end == null) {
            end = new InstantFilter();
        }
        return end;
    }

    public void setEnd(InstantFilter end) {
        this.end = end;
    }

    public StringFilter getReasonDescription() {
        return reasonDescription;
    }

    public StringFilter reasonDescription() {
        if (reasonDescription == null) {
            reasonDescription = new StringFilter();
        }
        return reasonDescription;
    }

    public void setReasonDescription(StringFilter reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public StatusFilter status() {
        if (status == null) {
            status = new StatusFilter();
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public LongFilter getWorkerId() {
        return workerId;
    }

    public LongFilter workerId() {
        if (workerId == null) {
            workerId = new LongFilter();
        }
        return workerId;
    }

    public void setWorkerId(LongFilter workerId) {
        this.workerId = workerId;
    }

    public LongFilter getReasonId() {
        return reasonId;
    }

    public LongFilter reasonId() {
        if (reasonId == null) {
            reasonId = new LongFilter();
        }
        return reasonId;
    }

    public void setReasonId(LongFilter reasonId) {
        this.reasonId = reasonId;
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
        final WorkHistoryCriteria that = (WorkHistoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(start, that.start) &&
            Objects.equals(toLunch, that.toLunch) &&
            Objects.equals(fromLunch, that.fromLunch) &&
            Objects.equals(end, that.end) &&
            Objects.equals(reasonDescription, that.reasonDescription) &&
            Objects.equals(status, that.status) &&
            Objects.equals(workerId, that.workerId) &&
            Objects.equals(reasonId, that.reasonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, start, toLunch, fromLunch, end, reasonDescription, status, workerId, reasonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkHistoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (start != null ? "start=" + start + ", " : "") +
            (toLunch != null ? "toLunch=" + toLunch + ", " : "") +
            (fromLunch != null ? "fromLunch=" + fromLunch + ", " : "") +
            (end != null ? "end=" + end + ", " : "") +
            (reasonDescription != null ? "reasonDescription=" + reasonDescription + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (workerId != null ? "workerId=" + workerId + ", " : "") +
            (reasonId != null ? "reasonId=" + reasonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
