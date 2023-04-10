package info.cleanflow.example.entity;

import java.time.LocalDate;

public class Party {

    private String name;

    private LocalDate start;

    private LocalDate end;

    public String getName() {
        if(name == null) {
            throw new IllegalStateException("not.null");
        }
        return name;
    }

    public void setName(String name) {
        if(name == null) {
            throw new IllegalArgumentException("not.null");
        }
        if(name.trim().isBlank()) {
            throw new IllegalArgumentException("not.blank");
        }
        this.name = name;
    }

    public LocalDate getStart() {
        if(start == null) {
            throw new IllegalStateException("not.null");
        }
        return start;
    }

    public void setStart(LocalDate start) {
        if(start == null) {
            throw new IllegalArgumentException("not.null");
        }
        if(start.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("not.future");
        }
        if(end != null) {
            throw new IllegalStateException("end.exists");
        }
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        if(start == null) {
            throw new IllegalStateException("start.does.not.exist");
        }
        if(end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("not.before.start");
        }
        this.end = end;
    }

}
