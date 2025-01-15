///**
// * 
// * I declare that this code was written by me, Lenovo. 
// * I will not copy or allow others to copy my code. 
// * I understand that copying code is considered as plagiarism.
// * 
// * Student Name: Lucas poh zhan le
// * Student ID: 22017175
// * Class: E63C
// * Date created: 2025-Jan-09 5:44:25 am 
// * 
// */
//
//package E63C.Lucas.LP01;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//
//@Entity
//public class Order {
//
//    @Id
//    private Integer id;
//    
//    @ManyToOne
//    private Member member;  // The member who made the order
//
//    @ManyToOne
//    private Item item;  // The item associated with the order
//
//    private int quantity;  // Quantity of the item ordered
//    private String orderId;
//    private String transactionId;
//    private double totalAmount;  // The total price of the order
//
//    // Constructor, getters, setters, etc.
//
//    public Order() {}
//
//    public Order(Member member, Item item, int quantity, String orderId, String transactionId, double totalAmount) {
//        this.member = member;
//        this.item = item;
//        this.quantity = quantity;
//        this.orderId = orderId;
//        this.transactionId = transactionId;
//        this.totalAmount = totalAmount;
//    }
//
//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	public Member getMember() {
//		return member;
//	}
//
//	public void setMember(Member member) {
//		this.member = member;
//	}
//
//	public Item getItem() {
//		return item;
//	}
//
//	public void setItem(Item item) {
//		this.item = item;
//	}
//
//	public int getQuantity() {
//		return quantity;
//	}
//
//	public void setQuantity(int quantity) {
//		this.quantity = quantity;
//	}
//
//	public String getOrderId() {
//		return orderId;
//	}
//
//	public void setOrderId(String orderId) {
//		this.orderId = orderId;
//	}
//
//	public String getTransactionId() {
//		return transactionId;
//	}
//
//	public void setTransactionId(String transactionId) {
//		this.transactionId = transactionId;
//	}
//
//	public double getTotalAmount() {
//		return totalAmount;
//	}
//
//	public void setTotalAmount(double totalAmount) {
//		this.totalAmount = totalAmount;
//	}
//    
//}