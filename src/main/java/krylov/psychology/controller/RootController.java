package krylov.psychology.controller;

import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.Product;
import krylov.psychology.model.Therapy;
import krylov.psychology.service.DayServiceImpl;
import krylov.psychology.service.DayTimeServiceImpl;
import krylov.psychology.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class RootController {
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private DayServiceImpl dayService;
    @Autowired
    private DayTimeServiceImpl dayTimeService;
    private final long day = 86400000;

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
    @GetMapping("record/{id}")
    public String recordingOnTherapy(@PathVariable(name = "id") long id,
                                     @RequestParam(name = "week", required = false) Integer week,
                                     Model model) {
        model.addAttribute("product", productService.findById(id));

        if (week == null || week < 0) {
            week = 0;
        }
        Date today = new Date();
        Date firstDate = new Date(today.getYear(), today.getMonth(), today.getDate() + week);
        Date lastDate = new Date(today.getYear(), today.getMonth(), today.getDate() + 5 + week);

        List<Day> listFromDB = dayService.findAllDayInPeriod(firstDate, lastDate);
        List<Day> localDayList = utilCreateLocalDayList(listFromDB, firstDate);

        model.addAttribute("d1", localDayList.get(0));
        model.addAttribute("d2", localDayList.get(1));
        model.addAttribute("d3", localDayList.get(2));
        model.addAttribute("d4", localDayList.get(3));
        model.addAttribute("d5", localDayList.get(4));
        model.addAttribute("before", week - 5);
        model.addAttribute("after", week + 5);
        return "recording_on_therapy.html";
    }
    @GetMapping("record/{productId}/{timeId}")
    public String recordingOnTherapy(Therapy therapy,
                                     @PathVariable(name = "productId") long productId,
                                     @PathVariable(name = "timeId") long timeId,
                                     Model model) {
        Product product = productService.findById(productId);
        DayTime dayTime = dayTimeService.findById(timeId);
        Day day1 = dayTime.getDay();
        model.addAttribute("product", product);
        model.addAttribute("day", day1);
        model.addAttribute("dayTime", dayTime);
        return "recording_on_therapy_client.html";
    }
    @GetMapping("confirm/{productId}/{timeId}")
    public String confirmRecordOnTherapy(@Valid Therapy therapy,
                                     BindingResult bindingResult,
                                     @PathVariable(name = "productId") long productId,
                                     @PathVariable(name = "timeId") long timeId,
                                     Model model) {
        Product product = productService.findById(productId);
        DayTime dayTime = dayTimeService.findById(timeId);
        Day day1 = dayTime.getDay();
        model.addAttribute("product", product);
        model.addAttribute("day", day1);
        model.addAttribute("dayTime", dayTime);
        if (bindingResult.hasErrors()) {
            return "recording_on_therapy_client.html";
        }
        System.out.println("product: " + product);
        System.out.println("day: " + day1.getDate());
        System.out.println("dayTime: " + dayTime.getLocalTime());
        System.out.println("name: " + therapy.getName());
        System.out.println("email: " + therapy.getEmail());
        System.out.println("phone: " + therapy.getPhoneNumber());

        return "all_products.html";
    }


    private  List<Day> utilCreateLocalDayList(List<Day> currentDayList, Date startDate) {
        List<Day> localDayList = new ArrayList<>();
        for (var i = 0; i < 5; i++) {
            Date newDate = new Date(startDate.getTime() + day * i);
            Day newDay = new Day(newDate);
            for (Day day: currentDayList) {
                if (day.getDate().getTime() == newDate.getTime()) {
                    newDay = day;
                }
            }
            localDayList.add(newDay);
        }
        return localDayList;
    }
}
