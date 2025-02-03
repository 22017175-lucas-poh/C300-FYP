/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-18 3:33:31 pm 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Lenovo
 *
 */
@Controller
public class HomeController {

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }

    @GetMapping("/calculator")
    public String calculator() {
        return "calculator";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            msg.setFrom("musashibestgirl990@gmail.com"); 

            javaMailSender.send(msg);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

	
    @PostMapping("/submitContact")
    public String submitContact(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("message") String message,
                                RedirectAttributes redirectAttributes) {
        // Compose the email subject and body
        String subject = "Contact Form Submission Received";
        String body = "Dear " + name + ",\n\nThank you for reaching out to us at RP Digital Bank.\n\n" +
                      "We have received your message:\n\n" +
                      "\"" + message + "\"\n\n" +
                      "Our team will get back to you shortly. If you have any urgent concerns, please do not hesitate to contact us directly.\n\n" +
                      "Best regards,\nRP Digital Bank Team";

        // Send email to the user
        try {
            sendEmail(email, subject, body);
            redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "There was an error sending your message. Please try again later.");
        }

        return "redirect:/contact";
    }
    
}

