package krylov.psychology.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(name = "day_times")
public class DayTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "local_time")
    @NotNull
    private LocalTime localTime;
    @Column(name = "time_is_free")
    private boolean timeIsFree;
    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;
    @OneToOne(mappedBy = "dayTime")
    private Therapy therapy;

    public DayTime() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public boolean isTimeIsFree() {
        return timeIsFree;
    }

    public void setTimeIsFree(boolean timeIsFree) {
        this.timeIsFree = timeIsFree;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Therapy getTherapy() {
        return therapy;
    }

    public void setTherapy(Therapy therapy) {
        this.therapy = therapy;
    }

    @Override
    public String toString() {
        return "DayTime{" +
                "localTime=" + localTime +
                ", timeIsFree=" + timeIsFree +
                '}';
    }
}
