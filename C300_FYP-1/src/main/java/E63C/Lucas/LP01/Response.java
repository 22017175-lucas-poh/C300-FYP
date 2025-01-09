/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2025-Jan-09 1:38:52 pm 
 * 
 */

package E63C.Lucas.LP01;

/**
 * @author Lenovo
 *
 */
public class Response {
    private String status;
    private String message;

    // Default constructor
    public Response() {}

    // Constructor to initialize status and message
    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
