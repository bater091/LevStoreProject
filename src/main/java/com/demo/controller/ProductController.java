package com.demo.controller;


import com.demo.service.BasketService;
import com.demo.service.ProductService;
import com.demo.service.UserService;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    String currentProductName;
    MultipartFile currentMultipartFile;
    Double currentPrice;
    private final BasketService basketService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, BasketService basketService, UserService userService) {
        this.productService = productService;
        this.basketService = basketService;
        this.userService = userService;
    }
    @GetMapping("")
    public ModelAndView back(){
        return new ModelAndView("index");
    }


    @GetMapping("/createProduct")
    public ModelAndView createProductView() {
        return new ModelAndView("createProduct");
    }

    @PostMapping("/createProduct=success")
    public void createProduct(HttpServletResponse response,HttpServletRequest request) throws IOException {
        productService.createProduct(currentMultipartFile, currentPrice, currentProductName, request.getUserPrincipal().getName() );

        currentPrice = null;
        currentMultipartFile = null;
        currentProductName = null;

        response.sendRedirect("/product");
    }


    @PostMapping("/lotByProductName")
    public ModelAndView getProductByProductName(@RequestParam String product) {
        currentProductName = product;
        ModelAndView modelAndView = new ModelAndView("getLotByProductName");
        modelAndView.addObject("fileName", productService.getProductByProductName(product).getFileName());
        modelAndView.addObject("price", productService.getProductByProductName(product).getPrice());
        modelAndView.addObject("productName", productService.getProductByProductName(product).getProductName());

        return modelAndView;
    }

    @GetMapping("/productMenu")
    public ModelAndView getAllProduct(@RequestParam(defaultValue = "marker", required = false) String marker) {

        ModelAndView modelAndView = new ModelAndView("productMenu");


        return modelAndView.addObject("products", productService.getAllProduct(marker));
    }

    @PostMapping("/deleteProduct")
    public void deleteProductByName(@RequestParam String product, HttpServletResponse response) throws IOException {
        productService.deleteByName(product);
        response.sendRedirect("/product");
    }

    @PostMapping("/confirm")
    public ModelAndView confirmProduct(@RequestParam("file") MultipartFile file,
                                       @RequestParam("price") Double price,
                                       @RequestParam("name") String productName) {
        this.productService.savePhoto(file);
        this.currentProductName = productName;
        this.currentMultipartFile = file;
        this.currentPrice = price;
        ModelAndView modelAndView = new ModelAndView("confirmProduct");
        modelAndView.addObject("price" , price);
        modelAndView.addObject("productName", productName);
        modelAndView.addObject("fileName", file.getOriginalFilename());
        return modelAndView;
    }

    @PostMapping("/addToBasket")
    public void addToBasket(@RequestParam String productName, @RequestParam String amount, HttpServletResponse response) throws IOException {
        basketService.addProduct(productService.getProductByProductName(productName), amount);
        log.info("SUCCESS ADD TO BASKET Added : " + productService.getProductByProductName(productName).toString() + "am");
        response.sendRedirect("/product/productMenu");

    }

    @GetMapping("/basket")
    public ModelAndView userBasket() {
        ModelAndView modelAndView = new ModelAndView("userBasket");
        modelAndView.addObject("basket", basketService.getUserBasket());
        return modelAndView;
    }

    @GetMapping("/clearBasket")
    public void clearBasket(HttpServletResponse response) throws IOException {
        basketService.clearBasket();
        response.sendRedirect("/product/productMenu");
    }
    @GetMapping("/profile")
    public ModelAndView userProfile(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("userProfile");
        modelAndView.addObject("products", productService.findUserProducts(
                userService.getUser(request.getUserPrincipal().getName()).getId()));
        modelAndView.addObject("username",request.getUserPrincipal().getName());
        return modelAndView;
    }

}
