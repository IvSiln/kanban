package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Subtask> subtasksList;
    private LocalDateTime finishTime;

    public Epic(String epicName, String epicDetail, Status status) {
        super(epicName, epicDetail, status);
        subtasksList = new ArrayList<>();
    }

    public List<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
        calculateEpicTime();
    }

    public void getActualStatus(Epic epic) {
        Status epicStatus;
        if (getSubtasksList().isEmpty()) {
            epicStatus = Status.NEW;
        } else {
            int countNew = 0;
            int countDone = 0;

            for (Subtask subtask : getSubtasksList()) {
                if (subtask.getStatus() == Status.DONE) {
                    countDone++;
                }
                if (subtask.getStatus() == Status.NEW) {
                    countNew++;
                }
                if (subtask.getStatus() == Status.IN_PROGRESS) {
                    break;
                }
            }

            if (countDone == getSubtasksList().size()) {
                epicStatus = Status.DONE;
            } else if (countNew == getSubtasksList().size()) {
                epicStatus = Status.NEW;
            } else {
                epicStatus = Status.IN_PROGRESS;
            }
        }
        epic.setStatus(epicStatus);
    }

    @Override
    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void calculateEpicTime() {
        if (!subtasksList.isEmpty()) {
            Duration sumDuration = null;
            LocalDateTime minDate = null;
            LocalDateTime maxDate = null;
            for (Subtask subtask : subtasksList) {
                if (subtask.getDuration() != null && subtask.getStartTime() != null) {
                    if (minDate == null || minDate.isAfter(subtask.getStartTime()))
                        minDate = subtask.getStartTime();
                    if (maxDate == null || maxDate.isBefore(subtask.getFinishTime()))
                        maxDate = subtask.getFinishTime();
                    if (sumDuration == null)
                        sumDuration = subtask.getDuration();
                    else
                        sumDuration = sumDuration.plus(subtask.getDuration());
                }
            }
            startTime = minDate;
            duration = sumDuration;
            finishTime = maxDate;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksList, epic.subtasksList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + subtasksList +
                ", name='" + getName() + '\'' +
                ", detail='" + getDetail() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", finishTime=" + getFinishTime() +
                ", duration=" + getDuration() +
                '}';
    }
}
