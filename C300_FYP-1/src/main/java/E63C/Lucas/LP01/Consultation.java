package E63C.Lucas.LP01;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Consultation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private LocalDate date;
    private LocalTime time;
    private String consultantName;

    // Constructors
    public Consultation() {}

    public Consultation(String userName, int id, LocalDate date, LocalTime time, String consultantName) {
        this.userName = userName;
        this.id = id;
        this.date = date;
        this.time = time;
        this.consultantName = consultantName;
    }

    // Getters and Setters
    public int getId() { 
    	return id; 
    	}
    public String getUserName() { 
    	return userName; 
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

    public void setId(int id) { 
    	this.id = id; 
    	}
    public void setUserName(String userName) { 
    	this.userName = userName; 
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