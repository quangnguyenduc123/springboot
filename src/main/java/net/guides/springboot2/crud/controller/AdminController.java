/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import net.guides.springboot2.crud.exception.ResourceNotFoundException;
import net.guides.springboot2.crud.model.Admin;
import net.guides.springboot2.crud.model.Cart;
import net.guides.springboot2.crud.model.Category;
import net.guides.springboot2.crud.model.Comment;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Detail;
import net.guides.springboot2.crud.model.Ingredient;
import net.guides.springboot2.crud.model.Product;
import net.guides.springboot2.crud.model.Supplier;
import net.guides.springboot2.crud.model.Import;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.Recipe;

import net.guides.springboot2.crud.repository.AdminRepository;
import net.guides.springboot2.crud.repository.CartRepository;
import net.guides.springboot2.crud.repository.CategoryRepository;
import net.guides.springboot2.crud.repository.CustomerRepository;
import net.guides.springboot2.crud.repository.DetailRepository;
import net.guides.springboot2.crud.repository.ImportRepository;
import net.guides.springboot2.crud.repository.IngredientRepository;
import net.guides.springboot2.crud.repository.OrderRepository;
import net.guides.springboot2.crud.repository.ProductRepository;
import net.guides.springboot2.crud.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.guides.springboot2.crud.repository.SupplierRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Owner
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private DetailRepository detailRepository;
    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private static String md5(String data)
            throws NoSuchAlgorithmException {
        // Get the algorithm:
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // Calculate Message Digest as bytes:
        byte[] digest = md5.digest(data.getBytes(UTF_8));
        // Convert to 32-char long String:
        return String.format("%032x%n", new BigInteger(1, digest));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Admin admin) throws NoSuchAlgorithmException {
        List<Admin> a = adminRepository.Login(admin.getUsername(), md5(admin.getPassword()));
        if (a.size() > 0) {
            return new ResponseEntity<Integer>(a.get(0).getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Integer>(-1, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/category")
    public ResponseEntity updateCategory(HttpServletRequest request, @RequestBody Category category) throws NoSuchAlgorithmException {
        Optional<Integer> a = Optional.ofNullable(Integer.parseInt(request.getHeader("admin")));
        if (a.isPresent()) {
            categoryRepository.getOne(category.getId());
            categoryRepository.save(category);
            return ResponseEntity.ok(HttpStatus.ACCEPTED);
        } else {
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/category")
    public ResponseEntity addCategory(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Category category) throws NoSuchAlgorithmException {

        category.setStatus(0);
        categoryRepository.save(category);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);

    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity deleteCategory(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id)
            throws ResourceNotFoundException {

        Category category = categoryRepository.getOne(id);
        category.setStatus(1);
        categoryRepository.save(category);
        List<Product> products = productRepository.FindCat(id);
        for (int i = 0; i < products.size(); i++) {
            Product temp = productRepository.getOne(products.get(i).getId());
            temp.setStatus(1);
            productRepository.save(temp);
            List<Order> orders = orderRepository.findByProcessedOrderByOrderdateDesc(0);
            for (Order order : orders) {
                cartRepository.deleteCartBYPro(products.get(i).getId(), order.getId());
            }

        }
        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("category/resell/{id}")
    public ResponseEntity reSellCategory(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id)
            throws ResourceNotFoundException {

        Category category = categoryRepository.getOne(id);
        category.setStatus(0);
        categoryRepository.save(category);
        categoryRepository.reSellCategory(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/supplier")
    public Supplier addSupplier(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Supplier supplier) throws NoSuchAlgorithmException {
        System.err.println(supplier.getPhonenumber() + "-" + supplier.getName());
        return supplierRepository.save(supplier);
    }

    @GetMapping("/supplier")
    public List<Supplier> listSuppliers(@RequestHeader("admin") @NotNull Integer admin) {
        return supplierRepository.findAll();
    }

    @PutMapping("/supplier")
    public Supplier updateSupplier(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Supplier supplier) throws NoSuchAlgorithmException {
        Supplier temp = supplierRepository.getOne(supplier.getId());
        temp.setAddress(supplier.getAddress());
        temp.setName(supplier.getName());
        temp.setPhonenumber(supplier.getPhonenumber());
        return supplierRepository.save(temp);
    }

    @PostMapping("/ingredient")
    public Ingredient addIngredient(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Ingredient ingredient) throws NoSuchAlgorithmException {
        if(ingredient.getMin()<=0)
            throw new NoSuchAlgorithmException();
        return ingredientRepository.save(ingredient);
    }

    @GetMapping("/ingredient")
    public List<Ingredient> listIngredients(@RequestHeader("admin") @NotNull Integer admin) {
        return ingredientRepository.findAll();
    }

    @PutMapping("/ingredient")
    public Ingredient updateIngredient(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Ingredient ingredient) throws NoSuchAlgorithmException {
        Ingredient temp = ingredientRepository.getOne(ingredient.getId());
        temp.setName(ingredient.getName());
        temp.setMin(ingredient.getMin());
        return ingredientRepository.save(temp);
    }

    @PostMapping("/import")
    public ResponseEntity addDetail(@RequestHeader("admin") @NotNull Integer admin, @RequestBody List<Detail> details) throws NoSuchAlgorithmException {

        Import a = new Import();
        a.setImportdate(new Date());
        int total = 0;
        for (Detail detail : details) {
            total = total + detail.getPrice();
        }
        a.setTotal(total);
        Import b = importRepository.save(a);
        System.err.println(b.getId());
        for (Detail detail : details) {
            detail.setImportid(b.getId());
        }
        detailRepository.saveAll(details);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/import")
    public List<Import> listImports(@RequestHeader("admin") @NotNull Integer admin) {
        return importRepository.findAll();
    }

    @GetMapping("/detail/{id}")
    public List<Detail> listDetails(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) {
        return detailRepository.listDetails(id);
    }

    @GetMapping("/detailimport/{supplierid}/{ingredientid}")
    public Map<String, String> detailImport(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "supplierid") Integer supplierid, @PathVariable(value = "ingredientid") Integer ingredientid) {
        Map<String, String> a = new HashMap<String, String>();
        Supplier b = supplierRepository.getOne(supplierid);
        Ingredient c = ingredientRepository.getOne(ingredientid);
        a.put("supplierid", b.getName());
        a.put("ingredientid", c.getName());
        return a;
    }

    @PostMapping("/importExcel")
    public ResponseEntity mapReapExcelDatatoDB(@RequestHeader("admin") @NotNull Integer admin, @RequestParam("file") MultipartFile reapExcelDataFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<Detail> details = new ArrayList<Detail>();
        Import a = new Import();
        a.setImportdate(new Date());
        int total = 0;
        for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            Detail temp = new Detail();
            Ingredient ingredient = ingredientRepository.findByName((row.getCell(1).getStringCellValue()));
             System.err.println(ingredient.toString());
            Supplier supplier = supplierRepository.findByName((row.getCell(0).getStringCellValue()));
             System.err.println(supplier.toString());
            temp.setIngredientid(ingredient.getId());
            temp.setSupplierid(supplier.getId());
            temp.setQuantity((int) (row.getCell(2).getNumericCellValue()));
            temp.setPrice((int) (row.getCell(3).getNumericCellValue()));
            total += (int) (row.getCell(3).getNumericCellValue());
           
            ingredient.setQuantity((int) (row.getCell(2).getNumericCellValue()) + ingredient.getQuantity());
            details.add(temp);
        }
        a.setTotal(total);
        a.setAdmin(admin);
        Import b = importRepository.save(a);
        for (Detail detail : details) {
            detail.setImportid(b.getId());
        }
        addImport(details);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Transactional
    public void addImport(List dataList) {
        detailRepository.saveAll(dataList);
    }

    @GetMapping("/products")
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/product")
    public ResponseEntity addProduct(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Product product) {
        productRepository.save(product);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PutMapping("/product")
    public ResponseEntity updateProduct(@RequestHeader("admin") @NotNull Integer admin, @RequestBody Product product) {
        Product temp = productRepository.getOne(product.getId());
        temp.setDescription(product.getDescription());
        temp.setPrice(product.getPrice());
        temp.setImage(product.getImage());
        temp.setName(product.getName());
        productRepository.save(temp);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity deleteProduct(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) {
        Product temp = productRepository.getOne(id);
        temp.setStatus(Math.abs(temp.getStatus() - 1));
        if (temp.getStatus() - 1 == 0) {
            List<Cart> carts = cartRepository.findByProductid(temp.getId());
            List<Order> orders = orderRepository.findByProcessedOrderByOrderdateDesc(0);
            for (Order order : orders) {
                cartRepository.deleteCartBYPro(temp.getId(), order.getId());
            }
        }
        productRepository.save(temp);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/recipe")
    public List<Recipe> listRecipes(@RequestHeader("admin") @NotNull Integer admin) {
        return recipeRepository.findAll();
    }

    @GetMapping("/detailRecipe/{id}")
    public Map<String, String> listDetailRecipes(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) {
        Map<String, String> a = new HashMap<String, String>();
        Recipe recipe = recipeRepository.getOne(id);
        Product c = productRepository.getOne(recipe.getProduct());
        Ingredient b = ingredientRepository.getOne(recipe.getIngredient());
        a.put("product", c.getName());
        a.put("ingredient", b.getName());
        return a;
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity delRecipe(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) {
        recipeRepository.deleteById(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @GetMapping("/recipe/{id}/{quantity}")
    public ResponseEntity updateRecipe(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id,@PathVariable(value = "quantity") Integer quantity) throws ResourceNotFoundException {
        Recipe temp = recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found for this id :: " + id));;
        temp.setQuantity(quantity);
        recipeRepository.save(temp);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/importRecipe")
    public ResponseEntity importRecipe(@RequestHeader("admin") @NotNull Integer admin, 
            @RequestParam("file") MultipartFile reapExcelDataFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<Recipe> details = new ArrayList<Recipe>();
        for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            Recipe temp = new Recipe();
            Ingredient ingredient = ingredientRepository.findByName((row.getCell(0).getStringCellValue()));
            Product product = productRepository.findByName((row.getCell(1).getStringCellValue()));
            temp.setIngredient(ingredient.getId());
            temp.setProduct(product.getId());
            temp.setQuantity((int) (row.getCell(2).getNumericCellValue()));
            
            details.add(temp);
        }
        importRecipe(details);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Transactional
    public void importRecipe(List dataList) {
        recipeRepository.saveAll(dataList);
    }

    @GetMapping("/order/{processed}")
    public List<Order> listFinshiedOrders(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "processed") Integer processed) {
        if (processed == 1) {
            return orderRepository.findByProcessedOrderByOrderdateDesc(4);
        } else {
            return orderRepository.findByProcessedBetweenOrderByOrderdateDesc(1, 3);
        }
    }

    @GetMapping("/orderCart/{id}")
    public List<Cart> orderCartsDetail(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) {
        return cartRepository.listCarts(id);
    }

    @GetMapping("/orderAdmin/{id}")
    public Order orderDetail(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) throws ResourceNotFoundException {
        Order a = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
        return a;
    }

    @GetMapping("/finishedOrder/{id}")
    public ResponseEntity finishedOrder(@RequestHeader("admin") @NotNull Integer admin,
            @PathVariable(value = "id") Integer id) throws ResourceNotFoundException {
        Order a = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
        Order temp = orderRepository.getOne(a.getId());
        if (a.getProcessed() == 3) {
            temp.setProcessed(4);
            orderRepository.save(temp);
            return ResponseEntity.ok(HttpStatus.ACCEPTED);
        }
        List<Cart> carts = new ArrayList<Cart>();

        carts = cartRepository.listCarts(id);
        for (int i = 0; i < carts.size(); i++) {
            List<Recipe> recipes = new ArrayList<Recipe>();
            recipes = recipeRepository.findByProduct(carts.get(i).getProductid());
            if (recipes.size() == 0) {
                return (ResponseEntity) ResponseEntity.badRequest();
            }
            for (Recipe recipe : recipes) {
                Ingredient ingredient = ingredientRepository.getOne(recipe.getIngredient());
            }
        }

        for (int i = 0; i < carts.size(); i++) {
            List<Recipe> recipes = new ArrayList<Recipe>();
            recipes = recipeRepository.findByProduct(carts.get(i).getProductid());
            for (Recipe recipe : recipes) {
                Ingredient ingredient = ingredientRepository.getOne(recipe.getIngredient());
                ingredient.setQuantity(ingredient.getQuantity() - (recipe.getQuantity()) * carts.get(i).getQuantity());
                ingredientRepository.save(ingredient);
            }
        }
        if (a.getProcessed() == 1) {
            temp.setProcessed(3);
        } else {
            temp.setProcessed(4);
        }
        orderRepository.save(temp);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/customer")
    public List<Object> listCustomers(@RequestHeader("admin") @NotNull Integer admin) throws ResourceNotFoundException {
        //Query q = em.createQuery("Select u.Id,u.fullname,u.Email,u.phonenumber,u.username,o.amount from Customers u LEFT JOIN(SELECT customerid , SUM(CASE WHEN processed =4 THEN total ELSE 0 END)as amount from orders GROUP BY orders.customerid ) o ON o.customerid=u.Id ORDER BY o.amount DESC");
        //Query q=em.createQuery("Select *from Orders");
        return customerRepository.listCustomers();

    }

    @GetMapping("/statistic/{id}")
    public Integer statistic(@RequestHeader("admin") @NotNull Integer admin, @PathVariable(value = "id") Integer id) throws ResourceNotFoundException {
//        List<Product> products=productRepository.findAll();
//        Map<String, Integer> a = new HashMap<String, Integer>();
//        for (Product product : products) {
//            int total=0;
//           List<Order> orders=orderRepository.ordersInMonth();
//            for (Order order : orders) {
//                List<Cart> carts=cartRepository.detailCarts(order.getId(), product.getId());
//                for (Cart cart : carts) {
//                    total+=cart.getQuantity();
//                }
//                a.put(product.getName(),total);
//            }
//        }
        int total = 0;
        List<Order> orders = orderRepository.ordersInMonth();
        for (Order order : orders) {
            List<Cart> carts = cartRepository.detailCarts(order.getId(), id);
            for (Cart cart : carts) {
                total += cart.getQuantity();
            }

        }
        return total;
    }

    @GetMapping("/check")
    public List<String> checkStrorage(HttpServletRequest request) throws ResourceNotFoundException {
        List<Order> orders = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
        Map<String, String> a = new HashMap<String, String>();
        List<String> results = new ArrayList<>();
        String temp = "";
        boolean flag = false;
        List<Cart> carts = cartRepository.listCarts(orders.get(0).getId());
        for (int i = 0; i < carts.size(); i++) {
            List<Recipe> recipes = new ArrayList<Recipe>();
            recipes = recipeRepository.findByProduct(carts.get(i).getProductid());
            for (Recipe recipe : recipes) {
                Ingredient ingredient = ingredientRepository.getOne(recipe.getIngredient());
                if (ingredient.getQuantity() - (recipe.getQuantity()) * carts.get(i).getQuantity() < 0) {
                    Product b = productRepository.getOne(carts.get(i).getProductid());
                    if (results.indexOf(b.getName() + ", ") == -1) {
                        results.add(b.getName() + ", ");
                    }
                    flag = true;
                }
            }
        }
        return results;
    }

    @GetMapping("/checkaddCart/{productId}/{amount}")
    public List<String> checkAddCart(HttpServletRequest request, @PathVariable(value = "productId") Integer productId, @PathVariable(value = "amount") Integer amount) throws ResourceNotFoundException {
        List<Order> orders = orderRepository.orders(Integer.parseInt(request.getHeader("login")));
        List<String> results = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<Recipe>();
        recipes = recipeRepository.findByProduct(productId);
        for (Recipe recipe : recipes) {
            Ingredient ingredient = ingredientRepository.getOne(recipe.getIngredient());
            if (ingredient.getQuantity() - (recipe.getQuantity()) * amount < 0) {
                Product b = productRepository.getOne(productId);
                results.add(b.getName());
                return results;
            }
        }

        return results;
    }
}
