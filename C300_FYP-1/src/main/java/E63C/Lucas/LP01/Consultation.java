package E63C.Lucas.LP01;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consultation {
    private int consultationNumber;
    private LocalDate date;
    private LocalTime time;
    private String consultantName;

    // Constructor
    public Consultation(int consultationNumber, LocalDate date, LocalTime time, String consultantName) {
        this.consultationNumber = consultationNumber;
        this.date = date;
        this.time = time;
        this.consultantName = consultantName;
    }

    // Getters
    public int getConsultationNumber() {
        return consultationNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getConsultantName() {
        return consultantName;
    }

    // Setters
    public void setConsultationNumber(int consultationNumber) {
        this.consultationNumber = consultationNumber;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }
}