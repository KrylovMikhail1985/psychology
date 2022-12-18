package krylov.psychology.controller;

import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.DefaultTime;
import krylov.psychology.model.Product;
import krylov.psychology.service.DayServiceImpl;
import krylov.psychology.service.DayTimeServiceImpl;
import krylov.psychology.service.DefaultTimeServiceImpl;
import krylov.psychology.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private DefaultTimeServiceImpl defaultTimeService;
    @Autowired
    private DayServiceImpl dayService;
    @Autowired
    private DayTimeServiceImpl dayTimeService;

    private final long day = 86400000;

    @GetMapping("")
    public String admin() {
        return "admin.html";
    }

    @GetMapping("/create_new_product")
    public String createNewProduct(Product product) {
        return "admin_new_product.html";
    }

    @PostMapping("/post_create_new_product")
    public String createNewProduct(@Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin_new_product.html";
        }
        productService.createProduct(
                product.getProductName(),
                product.getCost(),
                product.getDuration(),
                product.getDescription(),
                product.isActual(),
                product.getPriority());
        return "redirect:" + "/admin/admin_all_products?success=yes";
    }

    @GetMapping("/admin_all_products")
    public String showAllProductsForAdmin(@RequestParam(name = "success", required = false) String success,
                                          Model model) {
        if (success != null && success.equalsIgnoreCase("yes")) {
            model.addAttribute("success", 'Y');
        }
        model.addAttribute("listOfProducts", productService.findAllProducts());
        return "admin_all_products.html";
    }
    @GetMapping("/show_one_product/{id}")
    public String showOneProductsForAdmin(@PathVariable(name = "id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "admin_one_product.html";
    }
    @GetMapping("/update_product/{id}")
    public String updateProduct(@PathVariable(name = "id") long id,
                                Model model) {
        model.addAttribute("product", productService.findById(id));
        return "admin_update_product.html";

    }
    @PostMapping("/update_product/{id}")
    public String updateProductPost(@PathVariable(name = "id") long id,
                                    @Valid Product product, BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "admin_update_product.html";
        }
        productService.updateProduct(id, product);
        return "redirect:" + "/admin/admin_all_products?success=yes";
    }

    @GetMapping("/active_product/{id}")
    public String activeProduct(@PathVariable(name = "id") long id) {
        Product product = productService.findById(id);
        if (product.isActual()) {
            product.setActual(false);
        } else {
            product.setActual(true);
        }
        productService.updateProduct(id, product);
        return "redirect:" + "/admin/admin_all_products?success=yes";
    }
    @GetMapping("delete_product/{id}")
    public String deleteProductGet(@PathVariable(name = "id") long id) {
        productService.deleteProduct(id);
        return "redirect:" + "/admin/admin_all_products?success=yes";
    }

    @GetMapping("/active_new_month")
    public String activeNewMonth(Model model,
                                 @RequestParam(name = "next", required = false) String next) {
        Date date = new Date();
        int month = date.getMonth();
        int year = date.getYear();
        int dateDate = date.getDate();
        if (next != null && next.equalsIgnoreCase("yes")) {
            if (month == 11) {
                month = 0;
                year = year + 1;
            } else {
                month = month + 1;
            }
        }
        date = new Date(year, month, dateDate);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("countOfDays", utilCountOfDaysInMonth(date));
        return "admin_new_month.html";
    }
    @PostMapping("/active_new_month")
    public String activeNewMonth(@RequestParam(name = "month") Integer month,
                                 @RequestParam(name = "year") Integer year,
                                 @RequestBody MultiValueMap<String, String> formData,
                                 Model model) {
        formData.remove("month");
        formData.remove("year");

        //change MultiValueMap to List<Integer>
        List<Integer> listOfDays = new ArrayList<>();
        for (Map.Entry<String, List<String>> map: formData.entrySet()) {
            String str = map.getValue().get(0);
            listOfDays.add(Integer.parseInt(str));
        }
//        model.addAttribute("month", "Y");

        // find all created dates
        List<Day> allCreatedDaysAfterToday = dayService.findAllDayFromDate(new Date());
        List<Date> dateList = findAllDateFromDays(allCreatedDaysAfterToday);

        // create every new day
        List<DefaultTime> defaultTimeList = defaultTimeService.findAllDefaultTimeSortedByTime();
        for (int dateNumber: listOfDays) {
            Day day = new Day();
            day.setDate(new Date(year, month, dateNumber));
            if (!dateList.contains(day.getDate())) {
                List<DayTime> dayTimeList = dayTimeService.createListOfDayTimesFromDefaultTime(day, defaultTimeList);
                day.setDayTimes(dayTimeList);
                dayService.create(day);
            }
        }
        return "redirect:" + "/admin/all_days";
    }

    @GetMapping("default_time")
    public String showAllDefaultTime(Model model) {
        List<DefaultTime> list = defaultTimeService.findAllDefaultTimeSortedByTime();
        model.addAttribute("defaultTime", list);
        return "admin_all_default_time.html";
    }
    @GetMapping("new_default_time")
    public String newDefaultTime(DefaultTime defaultTime) {
        return "admin_new_default_time.html";
    }
    @PostMapping("new_default_time")
    public String newDefaultTimePost(@Valid DefaultTime localTime, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin_new_default_time.html";
        }
        defaultTimeService.createDefaultTime(localTime);
        model.addAttribute("success", 'Y');
        return "admin_new_default_time.html";
    }
    @GetMapping("delete_default_time/{id}")
    public String redirectOnDeleteMethod(@PathVariable(name = "id") long id) {
        defaultTimeService.deleteDefaultTime(id);
        return "redirect:" + "/admin/default_time";
    }

    @GetMapping("all_days")
    public String showAllWorkingDays(@RequestParam(name = "week", required = false) Integer week, Model model) {
        if (week == null || week < 0) {
            week = 0;
        }
        Date today = new Date();
        Date firstDate = new Date(today.getYear(), today.getMonth(), today.getDate() + week);
        Date lastDate = new Date(today.getYear(), today.getMonth(), today.getDate() + 5 + week);

        List<Day> listFromDB = dayService.findAllDayInPeriod(firstDate, lastDate);
        List<Day> localDayList = utilCreateLocalDayList(listFromDB, firstDate);
//        model.addAttribute("listOfDays", localDayList);
        model.addAttribute("d1", localDayList.get(0));
        model.addAttribute("d2", localDayList.get(1));
        model.addAttribute("d3", localDayList.get(2));
        model.addAttribute("d4", localDayList.get(3));
        model.addAttribute("d5", localDayList.get(4));
        model.addAttribute("before", week - 5);
        model.addAttribute("after", week + 5);
        return "admin_all_days.html";
    }
    @GetMapping("admin_one_day/{dataTimeLong}")
    public String showAdminOneDay(@PathVariable(name = "dataTimeLong") long dataTimeLong,
                                  Model model) {
        try {
            Day day = dayService.findDayByDate(new Date(dataTimeLong));
            List<DayTime> dayTimeList = day.getDayTimes();
            model.addAttribute("day", day);
            model.addAttribute("dayTimeList", dayTimeList);
            return "admin_one_day.html";
        } catch (Exception e) {
            model.addAttribute("dataTimeLong", new Date(dataTimeLong));
            model.addAttribute("defaultTimes", defaultTimeService.findAllDefaultTimeSortedByTime());
            return "admin_new_day.html";
        }
    }
    @GetMapping("daytime_active/{id}/{longDate}")
    public String activateDayTime(@PathVariable(name = "id") long id,
                                  @PathVariable(name = "longDate") long longDate,
                                  Model model) {
        dayTimeService.enableDisable(id);
        return "redirect:" + "/admin/admin_one_day/" + longDate;
    }
    @PostMapping("post_create_new_day")
    public String createNewDay(@RequestParam(name = "dataTime") long longDate) {
        List<DefaultTime> defaultTimeList = defaultTimeService.findAllDefaultTimeSortedByTime();
        utilCreateNewDay(new Date(longDate), defaultTimeList);
        return "redirect:" + "/admin/admin_one_day/" + longDate;
    }
    @GetMapping("delete_day/{id}")
    public String deleteDay(@PathVariable(name = "id") long id) {
        dayService.delete(id);
        return "redirect:" + "/admin/all_days";
    }
    @GetMapping("/test")
    public String test(Model model) {
        Date date = new Date(122, 11, 12);
        System.out.println(date);
//        Day day1 = new Day();
//        Day day2 = new Day();
//        Day day4 = new Day();
//        Day day5 = new Day();
//        Day day6 = new Day();
//        List<Integer> month = new ArrayList<>();
//        month.add(1);
//        month.add(2);
//        month.add(5);
//        month.add(6);
//        month.add(7);
//        model.addAttribute(month);
//        Month month = new Month();
//        month.setD1(true);
//        month.setD2(true);
//        month.setD3(false);
//        month.setD4(false);
//        month.setD5(true);
//        month.setD6(true);
//        month.setD7(false);
//        month.setMonth(11);
//        month.setYear(122);
//        model.addAttribute(month);
        return "admin.html";
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
    private Day utilCreateNewDay(Date date, List<DefaultTime> defaultTimeList) {
        Day day = new Day();
        day.setDate(date);
        List<DayTime> dayTimeList = dayTimeService.createListOfDayTimesFromDefaultTime(day, defaultTimeList);
        day.setDayTimes(dayTimeList);
        return dayService.create(day);
    }
    private int utilCountOfDaysInMonth(Date date) {
        final int year1900 = 1900;
        final int one = 1;
        int year = date.getYear() + year1900;
        int month = date.getMonth() + one;
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }
    private List<Date> findAllDateFromDays(List<Day> dayList) {
        List<Date> dateList = new ArrayList<>();
        for (Day day: dayList) {
            dateList.add(day.getDate());
        }
        return dateList;
    }
}