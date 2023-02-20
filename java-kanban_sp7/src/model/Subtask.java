package model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String subtaskName, String subtaskDetail, Status subtaskStatus, int epicId) {
        super(subtaskName, subtaskDetail, subtaskStatus);
        this.epicId = epicId;
    }

    public Subtask(String subtaskName, String subtaskDetail, Status subtaskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        super(subtaskName, subtaskDetail, subtaskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", finishTime=" + getFinishTime() +
                ", duration=" + (getDuration() == Duration.ZERO ? "" : getDuration())+
                '}';
    }
}
