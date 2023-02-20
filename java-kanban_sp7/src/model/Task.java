package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String detail;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String detail, Status status) {
        this.name = name;
        this.detail = detail;
        this.status = status;
    }

    public Task(String taskName, String detail, Status status, LocalDateTime startTime, Duration duration) {
        this.name = taskName;
        this.detail = detail;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getFinishTime() {
        return startTime.plusSeconds(duration.toSeconds());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(detail, task.detail) && Objects.equals(name, task.name) &&
                status == task.status && Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(detail, id, name, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", finishTime=" + (getDuration() == Duration.ZERO ? "" : getDuration()) +
                '}';
    }
}
