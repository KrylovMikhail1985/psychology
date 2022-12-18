package krylov.psychology.controller;

import krylov.psychology.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RootController {
    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/all_therapies")
    public String rootPage(Model model) {
        model.addAttribute("listOfProducts", productService.findAllProducts());
        return "all_products.html";
    }
    @GetMapping("product/{id}")
    public String showOneProduct(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "one_product.html";
    }
}
