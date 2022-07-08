package uz.devops.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkingTimeDTO {

    private long hours;
    private long minutes;

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public WorkingTimeDTO(long hours, long minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }
}
