///**
// * 
// * I declare that this code was written by me, Lenovo. 
// * I will not copy or allow others to copy my code. 
// * I understand that copying code is considered as plagiarism.
// * 
// * Student Name: Lucas poh zhan le
// * Student ID: 22017175
// * Class: E63C
// * Date created: 2025-Jan-09 5:41:26 am 
// * 
// */
//
//package E63C.Lucas.LP01;
//
//import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * @author Lenovo
// *
// */
//@Service
//public class ItemService {
//
//    @Autowired
//    private ItemRepository itemRepository;  // Assuming you have an ItemRepository to fetch data
//
//    public Item getItemById(int itemId) {
//        return itemRepository.findById(itemId).orElse(null);  // Fetch item by ID
//    }
//
//    public void saveItem(Item item) {
//        itemRepository.save(item);  // Save updated item
//    }
//}