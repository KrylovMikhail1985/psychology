package krylov.psychology.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "days")
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "date")
    @NotNull
    private Date date;
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL)
    private List<DayTime> dayTimes;

    public Day() {
    }

    public Day(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<DayTime> getDayTimes() {
        return dayTimes;
    }

    public void setDayTimes(List<DayTime> dayTimes) {
        this.dayTimes = dayTimes;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date=" + date +
                ", dayTimes=" + dayTimes +
                '}';
    }
}
