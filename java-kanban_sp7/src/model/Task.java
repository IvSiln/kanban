package model;

import java.time.Instant;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String detail;
    protected Status status;
    protected Instant startTime;
    protected long duration;
    long SECOND_IN_MINUTE = 60;

    public Task(String name, String detail, Status status) {
        this.name = name;
        this.detail = detail;
        this.status = status;
        this.startTime = Instant.now();
    }

    public Task(String taskName, String detail, Status status, Instant startTime, long duration) {
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getFinishTime() {
        return startTime.plusSeconds(SECOND_IN_MINUTE * duration);
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
                ", startTime=" + getStartTime().toEpochMilli() +
                ", finishTime=" + getFinishTime().toEpochMilli() +
                '}';
    }
}
