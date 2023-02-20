package model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime finishTime;

    public Epic(String epicName, String epicDetail, Status status) {
        super(epicName, epicDetail, status);
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(int id) {
        subtasksId.add(id);
    }

    @Override
    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + subtasksId +
                ", name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", finishTime=" + getFinishTime() +
                ", duration=" + getDuration() +
                '}';
    }
}
