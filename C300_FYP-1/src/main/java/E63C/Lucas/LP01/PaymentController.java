///**
// * 
// * I declare that this code was written by me, Lenovo. 
// * I will not copy or allow others to copy my code. 
// * I understand that copying code is considered as plagiarism.
// * 
// * Student Name: Lucas poh zhan le
// * Student ID: 22017175
// * Class: E63C
// * Date created: 2025-Jan-09 2:40:24 am 
// * 
// */
//
//package E63C.Lucas.LP01;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//public class PaymentController {
//
//    @Autowired
//    private CardService cardService; // Service to update the card balance
//
//    @PostMapping("/cart/process_order")
//    public String processOrder(@RequestParam("cartTotal") double cartTotal,
//                               @RequestParam("memberId") int memberId,
//                               @RequestParam("orderId") String orderId,
//                               @RequestParam("transactionId") String transactionId,
//                               Model model) {
//        // Call PayPal API here to verify the transaction
//        boolean isPaymentSuccessful = verifyPayPalPayment(orderId, transactionId, cartTotal);
//
//        if (isPaymentSuccessful) {
//            // Update the card balance upon success
//            boolean updated = cardService.updateCardBalance(memberId, cartTotal);
//            
//            if(updated) {
//                model.addAttribute("message", "Payment Successful. Your balance has been updated.");
//                return "success"; // Redirect or show a success page
//            } else {
//                model.addAttribute("error", "Error updating balance.");
//                return "error";
//            }
//        } else {
//            model.addAttribute("error", "Payment verification failed.");
//            return "error";
//        }
//    }
//
//    private boolean verifyPayPalPayment(String orderId, String transactionId, double amount) {
//        // Call the PayPal API or verify payment status (you may use PayPal SDK here)
//        // Assuming verification passes for now
//        return true; // For now, we simulate a successful transaction.
//    }
//}
//
