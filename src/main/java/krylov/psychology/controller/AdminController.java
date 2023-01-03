package krylov.psychology.controller;

import krylov.psychology.mail.EmailServiceImpl;
import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.DefaultTime;
import krylov.psychology.model.MyInformation;
import krylov.psychology.model.Product;
import krylov.psychology.model.Therapy;
import krylov.psychology.security.jwt.JwtTokenProvider;
import krylov.psychology.service.DayServiceImpl;
import krylov.psychology.service.DayTimeServiceImpl;
import krylov.psychology.service.DefaultTimeServiceImpl;
import krylov.psychology.service.MyInformationServiceImpl;
import krylov.psychology.service.ProductServiceImpl;
import krylov.psychology.service.TherapyServiceImpl;
import krylov.psychology.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private DefaultTimeServiceImpl defaultTimeService;
    @Autowired
    private DayServiceImpl dayService;
    @Autowired
    private DayTimeServiceImpl dayTimeService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TherapyServiceImpl therapyService;
    @Autowired
    private MyInformationServiceImpl myInformationService;

    private final long dayInt = 86400000;

    @GetMapping("")
    public String admin() {
        return "admin.html";
    }
    @GetMapping("/login")
    public String login() {
        return "admin_login.html";
    }
    @PostMapping("/login")
    public String loginPost(@RequestParam(name = "login") String login,
                            @RequestParam(name = "password") String password,
                            HttpServletResponse response) {
        String token;

        if (userAndPasswordIsCorrect(login, password)) {
            token = jwtTokenProvider.createToken(login);
            Cookie loginCookie = new Cookie("auth_token", token);
            // living time in seconds
            loginCookie.setMaxAge(60 * 60);
            response.addCookie(loginCookie);
            return "admin.html";
        }
        return "admin_login.html";
    }

    @GetMapping("/create_new_product")
    public String createNewProduct(Product product) {
        return "admin_new_product.html";
    }

    @PostMapping("/post_create_new_product")
    public String createNewProduct(@Valid Product product, BindingResult bindingResult) {
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
    public String showAllProductsForAdmin(Model model) {
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
    public String updateProductPost(@Valid Product product,
                                    BindingResult bindingResult,
                                    @PathVariable(name = "id") long id) {
        if (bindingResult.hasErrors()) {
            return "admin_update_product.html";
        }
        productService.updateProduct(id, product);
        return "redirect:" + "/admin/admin_all_products";
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
        return "redirect:" + "/admin/admin_all_products";
    }
    @GetMapping("delete_product/{id}")
    public String deleteProductGet(@PathVariable(name = "id") long id,
                                   Model model) {
        List<Therapy> therapyList = therapyService.findTherapyWithProductId(id);
        if (therapyList.size() > 0) {
            model.addAttribute("product", productService.findById(id));
            model.addAttribute("thereIsTherapyWithThisProduct", true);
            return "admin_one_product.html";
        }
        productService.deleteProduct(id);
        return "redirect:" + "/admin/admin_all_products";
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
        model.addAttribute("countOfDays", Util.countOfDaysInMonth(date));
        return "admin_new_month.html";
    }
    @PostMapping("/active_new_month")
    public String activeNewMonth(@RequestParam(name = "month") Integer month,
                                 @RequestParam(name = "year") Integer year,
                                 @RequestBody MultiValueMap<String, String> formData) {
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
        List<Date> dateList = Util.findAllDateFromDays(allCreatedDaysAfterToday);

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
        List<Day> localDayList = Util.createLocalDayList(listFromDB, firstDate);
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
            boolean thereIsNoTherapyThisDay = true;
            for (DayTime dayTime: dayTimeList) {
                Therapy therapy = dayTime.getTherapy();
                if (therapy != null) {
                    thereIsNoTherapyThisDay = false;
                    break;
                }
            }
            model.addAttribute("thereIsNoTherapyThisDay", thereIsNoTherapyThisDay);
            return "admin_one_day.html";
        } catch (Exception e) {
            model.addAttribute("dataTimeLong", new Date(dataTimeLong));
            model.addAttribute("defaultTimes", defaultTimeService.findAllDefaultTimeSortedByTime());
            return "admin_new_day.html";
        }
    }
    @GetMapping("daytime_active/{dayTimeId}/{longDate}")
    public String activateDayTime(@PathVariable(name = "dayTimeId") long dayTimeId,
                                  @PathVariable(name = "longDate") long longDate,
                                  Model model) {
        DayTime dayTime = dayTimeService.findById(dayTimeId);
        if (Util.thereIsNoTherapyInThisTime(dayTime)) {
            dayTimeService.enableDisable(dayTimeId);
        }
        return "redirect:" + "/admin/admin_one_day/" + longDate;
    }
    @PostMapping("post_create_new_day")
    public String createNewDay(@RequestParam(name = "dataTime") long longDate) {
        List<DefaultTime> defaultTimeList = defaultTimeService.findAllDefaultTimeSortedByTime();
        Day day = new Day();
        day.setDate(new Date(longDate));
        List<DayTime> dayTimeList = dayTimeService.createListOfDayTimesFromDefaultTime(day, defaultTimeList);
        day.setDayTimes(dayTimeList);
        dayService.create(day);
        return "redirect:" + "/admin/admin_one_day/" + longDate;
    }
    @GetMapping("delete_day/{id}")
    public String deleteDay(@PathVariable(name = "id") long id) {
        Day day = dayService.findById(id);
        for (DayTime dayTime: day.getDayTimes()) {
            if (!dayTime.isTimeIsFree()) {
                return "redirect:" + "/admin/all_days";
            }
        }
        dayService.delete(id);
        return "redirect:" + "/admin/all_days";
    }
    @GetMapping("one_therapy/{dayTimeId}")
    public String onrTherapy(@PathVariable(name = "dayTimeId") long dayTimeId,
                             Model model) {
        try {
            DayTime dayTime = dayTimeService.findById(dayTimeId);
            Therapy therapy = dayTime.getTherapy();
            Product product = therapy.getProduct();
            Day day = dayTime.getDay();
            model.addAttribute("day", day);
            model.addAttribute("dayTime", dayTime);
            model.addAttribute("product", product);
            model.addAttribute("therapy", therapy);
        } catch (Exception e) {
            System.out.println("There is not therapy in dayTime with dayTimeID: " + dayTimeId);
        }
        return "admin_therapy.html";
    }
    @PostMapping("delete_therapy/{therapyId}")
    public String deleteTherapy(@PathVariable(name = "therapyId") long therapyId,
                                 @RequestParam(name = "y", required = false) String  y,
                                 @RequestParam(name = "e", required = false) String  e,
                                 @RequestParam(name = "s", required = false) String  s) {
        Therapy therapy = therapyService.findById(therapyId);
        Day day = therapy.getDayTime().getDay();
        long dayLong = day.getDate().getTime();
        if (y != null && e != null && s != null) {
            LocalTime localTimeTherapy = therapy.getDayTime().getLocalTime();
            Day dayFromDelTherapy =
                    Util.thisDayWithActivatedDayTime(day, localTimeTherapy, therapy.getProduct().getDuration());
            dayService.update(dayFromDelTherapy.getId(), dayFromDelTherapy);
            therapyService.deleteById(therapyId);
        }
        return "redirect:" + "/admin/admin_one_day/" + dayLong;
    }
    @GetMapping("transfer_therapy/{therapyId}")
    public String transferTherapy(@PathVariable(name = "therapyId") long therapyId,
                                  @RequestParam(name = "week", required = false) Integer week,
                                  Model model) {
        if (week == null || week < 0) {
            week = 0;
        }

        Therapy therapy = therapyService.findById(therapyId);
        Product product = therapy.getProduct();
        model.addAttribute("therapy", therapy);
        model.addAttribute("product", product);

        Date today = new Date();
        Date tomorrow = new Date(today.getTime());
        Date firstDate = new Date(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDate() + week);
        Date lastDate = new Date(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDate() + 5 + week);

        List<Day> listFromDB = dayService.findAllDayInPeriod(firstDate, lastDate);
        List<Day> listWithFitTime = Util.disableNotFitTime(listFromDB, product.getDuration());
        List<Day> localDayList = Util.createLocalDayList(listWithFitTime, firstDate);

        model.addAttribute("d1", localDayList.get(0));
        model.addAttribute("d2", localDayList.get(1));
        model.addAttribute("d3", localDayList.get(2));
        model.addAttribute("d4", localDayList.get(3));
        model.addAttribute("d5", localDayList.get(4));
        model.addAttribute("before", week - 5);
        model.addAttribute("after", week + 5);
        return "admin_transfer_therapy.html";
    }
    @GetMapping("transfer/{therapyId}/{dayTimeId}")
    public String transferTherapyConfirm(@PathVariable(name = "therapyId") long therapyId,
                                         @PathVariable(name = "dayTimeId") long dayTimeId,
                                         Model model) {
        Therapy therapy = therapyService.findById(therapyId);
        DayTime dayTime = dayTimeService.findById(dayTimeId);
        long oldDayLong = therapy.getDayTime().getDay().getDate().getTime();

        //create new therapy
        Therapy newTherapy = new Therapy(therapy.getEmail(), therapy.getName(), therapy.getPhoneNumber());
        newTherapy.setProduct(therapy.getProduct());
        newTherapy.setDayTime(dayTime);

        //occupy time in the day and write therapy
        Day thisDayDeactivatedTime = Util.thisDayWithDeactivatedDayTimeIfNoTherapy(
                        dayTime.getDay(),
                        dayTime.getLocalTime(),
                        therapy.getProduct().getDuration());
        dayService.update(thisDayDeactivatedTime.getId(), thisDayDeactivatedTime);
        therapyService.createTherapy(newTherapy);

        //delete old therapy
        Day day = therapy.getDayTime().getDay();
        LocalTime localTimeTherapy = therapy.getDayTime().getLocalTime();
        Day dayFromDelTherapy =
                Util.thisDayWithActivatedDayTime(day, localTimeTherapy, therapy.getProduct().getDuration());
        dayService.update(dayFromDelTherapy.getId(), dayFromDelTherapy);
        therapyService.deleteById(therapyId);


        //message for client
        String message = Util.textMessageForClientForTransfer(newTherapy);
        emailService.sendSimpleMessage(therapy.getEmail(), "Перенос встречи", message);

        return "redirect:" + "/admin/admin_one_day/" + oldDayLong;
    }
    @GetMapping("/info")
    public String updateInfo(MyInformation myInformation, Model model) {
        myInformation = myInformationService.find();
        model.addAttribute("information", myInformation);
        return "admin_information.html";
    }
    @PostMapping("/update_info")
    public String updateInfoPost(MyInformation myInformation,
                                 Model model) {
        MyInformation info = myInformationService.find();
        info.setShortInformation(myInformation.getShortInformation());
        myInformationService.save(info);
        return "index.html";
    }
    @GetMapping("/update_login_info")
    public String updateLoginInfo(Model model) {
        return "admin_update_password.html";
    }
    @PostMapping("/update_login_info")
    public String updateLoginInfoPost(@RequestParam(name = "old_login") String oldLogin,
                                      @RequestParam(name = "old_password") String oldPassword,
                                      @RequestParam(name = "new_login") String newLogin,
                                      @RequestParam(name = "new_password") String newPassword,
                                      Model model) {
        MyInformation myInformation = myInformationService.find();
        if (myInformation.getLogin().equals(oldLogin) && encoder.matches(oldPassword, myInformation.getPassword())) {
            myInformation.setLogin(newLogin);
            myInformation.setPassword(encoder.encode(newPassword));
            myInformationService.save(myInformation);
            return "redirect:" + "/admin";
        }
        model.addAttribute("wrong", true);
        return "admin_update_password.html";
    }


    private boolean userAndPasswordIsCorrect(String userName, String password) {
        MyInformation myInformation = new MyInformation();
        try {
            myInformation = myInformationService.find();
        } catch (Exception e) {
            myInformation.setLogin("admin");
            myInformation.setPassword(encoder.encode("admin"));
            myInformationService.save(myInformation);
        }
        String adminName = myInformation.getLogin();
        String adminPassword = myInformation.getPassword();

        if (userName.equals(adminName) && encoder.matches(password, adminPassword)) {
            return true;
        } else {
            System.out.println("User or password is not correct!");
            return false;
        }
    }
}
