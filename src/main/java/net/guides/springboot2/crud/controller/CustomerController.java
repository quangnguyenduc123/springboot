package net.guides.springboot2.crud.controller;

import java.math.BigInteger;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.guides.springboot2.crud.exception.ResourceNotFoundException;
import net.guides.springboot2.crud.model.Cart;
import net.guides.springboot2.crud.model.Category;
import net.guides.springboot2.crud.model.Comment;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.OrderDetail;
import net.guides.springboot2.crud.model.Product;
import net.guides.springboot2.crud.repository.CartRepository;
import net.guides.springboot2.crud.repository.CategoryRepository;
import net.guides.springboot2.crud.repository.CommentRepository;
import net.guides.springboot2.crud.repository.CustomerRepository;
import net.guides.springboot2.crud.repository.OrderDetailRepository;
import net.guides.springboot2.crud.repository.OrderRepository;
import net.guides.springboot2.crud.repository.ProductRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    MailerService mailer;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CommentRepository commentRepository;
    @GetMapping("/profile/{id}")
    public ResponseEntity<Customer> getEmployeeById(@PathVariable(value = "id") Integer cusId)
            throws ResourceNotFoundException {

        Customer customer = customerRepository.findById(cusId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + cusId));
        return ResponseEntity.ok().body(customer);
    }

    @PostMapping("/add")
    public Customer createCustomers(@RequestBody Customer customer) throws NoSuchAlgorithmException {

        customer.setPassword(md5(customer.getPassword()));
        customer.setStatus(0);
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomers(@PathVariable(value = "id") Integer cusId,
            @Valid @RequestBody Customer cusDetails) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(cusId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id : " + cusId));

        customer.setFullname(cusDetails.getFullname());
        customer.setPassword(cusDetails.getPassword());
        customer.setPhonenumber(cusDetails.getPhonenumber());
        customer.setAddress(cusDetails.getAddress());
        final Customer updatedEmployee = customerRepository.save(customer);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteCustomers(@PathVariable(value = "id") Integer cusId)
            throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(cusId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + cusId));

        customerRepository.delete(customer);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Integer> Login(@Valid @RequestBody Map<String, String> json) throws NoSuchAlgorithmException {
        ArrayList<String> users = new ArrayList<String>();
        for (Map.Entry m : json.entrySet()) {
            String a = (String) m.getValue();
            users.add(a);

        }
        users.set(1, md5(users.get(1)));

        List<Customer> customer = customerRepository.Login(users.get(0), users.get(1));
        if (customer.size() > 0) {
            return new ResponseEntity<Integer>(customer.get(0).getId(), HttpStatus.OK);

        } else {
            return new ResponseEntity<Integer>(-1, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/facebook")
    public Map<String, Integer> loginFacebook(@RequestBody Customer customer) throws NoSuchAlgorithmException {
        customer.setPassword(md5(customer.getPassword()));
        List<Customer> customers = customerRepository.Loginfb(customer.getUsername());

        Map<String, Integer> response = new HashMap<>();
        if (customers.size() > 0) {
            response.put("have", customers.get(0).getId());
        } else {
            customer.setPassword(md5(customer.getPassword()));
            customer.setStatus(0);
            Customer a = customerRepository.save(customer);
            response.put("not", a.getId());
        }
        return response;
    }

    private static String md5(String data)
            throws NoSuchAlgorithmException {
        // Get the algorithm:
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // Calculate Message Digest as bytes:
        byte[] digest = md5.digest(data.getBytes(UTF_8));
        // Convert to 32-char long String:
        return String.format("%032x%n", new BigInteger(1, digest));
    }
    //lấy order của người đó xong kiểm tra để lưu phone và địa chỉ của người mua nếu chưa có
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(HttpServletRequest request,@RequestBody Order order) {
        List<Order> orders = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
       
        Order order1=orderRepository.getOne(orders.get(0).getId());
        Customer customer =customerRepository.getOne(Integer.parseInt(request.getHeader("login")));
        if(customer.getAddress().compareToIgnoreCase("0")==0){
             customer.setAddress(order.getAddress());
        }
        if(customer.getPhonenumber().compareToIgnoreCase("0")==0){
            customer.setPhonenumber(order.getPhone());
        }
        
        customerRepository.save(customer);
        order1.setAddress(order.getAddress());
        order1.setPhone(order.getPhone());
        order1.setMethod(order.getMethod());
        if(order.getMethod()==0){
            order1.setProcessed(1);
        }
        else
            order1.setProcessed(2);
        order1.setOrderdate(new Date());
        order1.setTotal(order.getTotal());
        return new ResponseEntity<Order>(orderRepository.save(order1), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/forgot/{username}")
    public ResponseEntity<String> forgotPass(@PathVariable(value = "username") String username) throws Exception {
        List<Customer> customers = customerRepository.Loginfb(username);
        if (customers.size() > 0) {
            String to = customers.get(0).getEmail();
            String subject = "Reset password";
            String body = "Your new password is : 1. You need to change your password due to account proctection";
            customers.get(0).setPassword(md5("1"));
            customerRepository.save(customers.get(0));
            mailer.send(to, subject, body);
            return new ResponseEntity<String>("Ok", HttpStatus.OK);
        } else {

            return new ResponseEntity<String>("Falied", HttpStatus.BAD_REQUEST);
        }

    }
    //Khi bấm vào chi tiết mặt hàng , xem coi mặt hàng đó có trong giỏ hàng không và số lượng bao nhiêu  /hoặc ở trang yourcart là tính tất cả các sản phẩm trong giỏ hàng 
    @GetMapping("/orderCart")
    public ResponseEntity<List<Cart>> orderCart(HttpServletRequest request)
            throws ResourceNotFoundException {
        List<Order> orders = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
        if (orders.size() > 0) {
            return new ResponseEntity<List<Cart>>(cartRepository.listCarts(orders.get(0).getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<List<Cart>>(HttpStatus.NOT_FOUND);
        }
    }
    // Khi bấm nút đặt hàng tỏng trang chi tiết sản phẩm, kiểm tra có giỏ hàng nào ko ( process=0), chưa có thì tạo
    // có rồi thì thêm sp hoặc update số lượng
    @GetMapping("/updateOrder/{productId}/{quantity}")
    public ResponseEntity updateOrder(HttpServletRequest request,@PathVariable(value = "productId") Integer productId,@PathVariable(value = "quantity") Integer quantity) throws NoSuchAlgorithmException, ResourceNotFoundException {
        List<Order> orders = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
        if (orders.size() <= 0) {
            Date a = new Date();
            int cusid = Integer.parseInt(request.getHeader("login"));
            Customer customer = customerRepository.findById(cusid)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id : " + cusid));
            String temp = "a";
            Order order = new Order();
            order.setCustomerid(cusid);
            order.setAddress(customer.getAddress());
            order.setMethod(0);
            order.setPhone(customer.getPhonenumber());
            order.setProcessed(0);
            order.setTotal(0);
            order.setOrderdate(a);
            orderRepository.save(order);
        }
        List<Order> orders2 = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
        List<Cart> cart2=cartRepository.detailCarts(orders2.get(0).getId(), productId);
        if(cart2.size()<=0){
            Cart cart=new Cart();
            cart.setOrderid(orders2.get(0).getId());
            cart.setProductid(productId);
            cart.setQuantity(quantity);
            Product product =productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found for this id : " ));
            cart.setPrice(product.getPrice());
            cartRepository.save(cart);
        }
        else{
            Cart cart3=cartRepository.getOne(cart2.get(0).getId());
            cart3.setQuantity(quantity);
            cartRepository.save(cart3);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
    // Đây là chỉnh sửa số lượng mặt hàng ở trang thanh toán
    @GetMapping("/updateQuantity/{id}/{quantity}")
    public ResponseEntity updateCartQuantity(HttpServletRequest request,@PathVariable(value = "id") Integer id,@PathVariable(value = "quantity") Integer quantity)
            throws ResourceNotFoundException {
        
        Cart cart = cartRepository.getOne(id);
        cart.setQuantity(quantity);
        cartRepository.save(cart);
        return new ResponseEntity(HttpStatus.OK);
    }
    // Xóa mặt hàng ở trang thanh toán
    @GetMapping("/removeItem/{id}")
    public ResponseEntity removeItem(HttpServletRequest request,@PathVariable(value = "id") Integer id)
            throws ResourceNotFoundException {
        
        Cart cart = cartRepository.getOne(id);
        cartRepository.delete(cart);
        return new ResponseEntity(HttpStatus.OK);
    }
    @PostMapping("/feedbacks")
    public Comment createFeedback(@RequestBody Comment comment) throws NoSuchAlgorithmException {
        return commentRepository.save(comment);
    }
    @DeleteMapping("/feedbacks/{id}")
    public ResponseEntity deleteFeedback(@PathVariable(value = "id") Integer id) throws NoSuchAlgorithmException, ResourceNotFoundException {
        Comment comment=commentRepository.findById(id) .orElseThrow(() -> new ResourceNotFoundException("Not Found " ));
        commentRepository.delete(comment);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/processing")
    public ResponseEntity processingOrders(HttpServletRequest request)
            throws ResourceNotFoundException {
        
         return new ResponseEntity<List<Order>>(orderRepository.processingOrders(Integer.parseInt(request.getHeader("login"))),HttpStatus.OK);
    }
     @GetMapping("/finished")
    public ResponseEntity finishedOrders(HttpServletRequest request)
            throws ResourceNotFoundException {
        
        return new ResponseEntity<List<Order>>(orderRepository.finishedOrders(Integer.parseInt(request.getHeader("login"))),HttpStatus.OK);
    }
    
    
    
    @GetMapping("/carts/{id}")
    public ResponseEntity Carts(HttpServletRequest request,@PathVariable(value = "id") Integer id)
            throws ResourceNotFoundException {
        
        return new ResponseEntity<List<Cart>>(cartRepository.listCarts(id),HttpStatus.OK);
    }
}
