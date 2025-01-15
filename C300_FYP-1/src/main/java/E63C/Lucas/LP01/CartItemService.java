///**
// * 
// * I declare that this code was written by me, Lenovo. 
// * I will not copy or allow others to copy my code. 
// * I understand that copying code is considered as plagiarism.
// * 
// * Student Name: Lucas poh zhan le
// * Student ID: 22017175
// * Class: E63C
// * Date created: 2025-Jan-09 5:34:57 am 
// * 
// */
//
//package E63C.Lucas.LP01;
//
//import java.util.List;
//import E63C.Lucas.LP01.Item;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * @author Lenovo
// *
// */
//@Service
//public class CartItemService {
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    // Method to retrieve all cart items by member ID
//    public List<CartItem> getCartItemsByMemberId(int memberId) {
//        return cartItemRepository.findByMemberId(memberId);
//    }
//
//    // Method to delete a cart item by its ID
//    public void deleteCartItem(int cartItemId) {
//        cartItemRepository.deleteById(cartItemId);
//    }
//}