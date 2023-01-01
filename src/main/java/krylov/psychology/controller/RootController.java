package krylov.psychology.controller;

import krylov.psychology.mail.EmailServiceImpl;
import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.MyInformation;
import krylov.psychology.model.Product;
import krylov.psychology.model.Therapy;
import krylov.psychology.service.DayServiceImpl;
import krylov.psychology.service.DayTimeServiceImpl;
import krylov.psychology.service.MyInformationServiceImpl;
import krylov.psychology.service.ProductServiceImpl;
import krylov.psychology.service.TherapyServiceImpl;
import krylov.psychology.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class RootController {
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private DayServiceImpl dayService;
    @Autowired
    private DayTimeServiceImpl dayTimeService;
    @Autowired
    private TherapyServiceImpl therapyService;
    @Autowired
    private MyInformationServiceImpl myInformationService;
    @Value("${email}")
    private String email;
    private final long dayInt = 86400000;
    @GetMapping("/")
    public String root(Model model) {
        MyInformation myInformation = myInformationService.find();
        model.addAttribute("info", myInformation.getShortInformation());
        return "index.html";
    }
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
                                     @RequestParam(name = "timeIsOccupy", required = false) boolean timeIsOccupy,
                                     Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);

        if (week == null || week < 0) {
            week = 0;
        }
        if (timeIsOccupy) {
            model.addAttribute("timeIsOccupy", true);
        }

        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + dayInt);
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
        Day day = dayTime.getDay();

        model.addAttribute("product", product);
        model.addAttribute("day", day);
        model.addAttribute("dayTime", dayTime);
        if (bindingResult.hasErrors()) {
            return "recording_on_therapy_client.html";
        }

        therapy.setProduct(product);
        therapy.setDayTime(dayTime);

        String codeForConfirmation = Util.randomForeSymbolCode();
        model.addAttribute("therapy", therapy);
        model.addAttribute("randomForeSymbolCode", codeForConfirmation);
        String message = Util.textMessageForClientConfirmation(therapy, codeForConfirmation);
        emailService.sendSimpleMessage(therapy.getEmail(), "Подтверждение записи", message);
        return "recording_on_therapy_client_confirm.html";
    }
    @GetMapping("confirm2/{productId}/{timeId}")
    public String confirm2RecordOnTherapy(Therapy therapy,
                                         @PathVariable(name = "productId") long productId,
                                         @PathVariable(name = "timeId") long timeId,
                                         @RequestParam("randomForeSymbolCode") String randomForeSymbolCode,
                                         @RequestParam("inputForeSymbolCode") String inputForeSymbolCode,
                                         Model model) {
        inputForeSymbolCode = inputForeSymbolCode.trim();
        Product product = productService.findById(productId);
        DayTime dayTime = dayTimeService.findById(timeId);
        Day day = dayTime.getDay();

        model.addAttribute("product", product);
        model.addAttribute("day", day);
        model.addAttribute("dayTime", dayTime);

        therapy.setProduct(product);
        therapy.setDayTime(dayTime);
        therapy.setCreatedAt(new Date());

        if (!randomForeSymbolCode.equals(inputForeSymbolCode)) {
            model.addAttribute("codeIsNotCorrect", true);
            String codeForConfirmation = Util.randomForeSymbolCode();
            model.addAttribute("randomForeSymbolCode", codeForConfirmation);
            String message = Util.textMessageForClientConfirmation(therapy, codeForConfirmation);
            emailService.sendSimpleMessage(therapy.getEmail(), "Подтверждение записи", message);
            return "recording_on_therapy_client_confirm.html";
        }

        try {
            Day thisDayDeactivatedTime =
                    Util.thisDayWithDeactivatedDayTimeIfNoTherapy(day, dayTime.getLocalTime(), product.getDuration());
            dayService.update(thisDayDeactivatedTime.getId(), thisDayDeactivatedTime);
            therapyService.createTherapy(therapy);
            emailService.sendSimpleMessage(
                    therapy.getEmail(),
                    "Запись подтверждена",
                    "Запись успешно подтверждена!");
        } catch (Exception e) {
            return "redirect:" + "/record/" + product.getId() + "?timeIsOccupy=true";
        }
        return "recording_on_therapy_success.html";
    }
    @Autowired
    private PasswordEncoder encoder;
    @GetMapping("/test")
    public String test() {
//    MyInformation myInformation = myInformationService.find();
//    myInformation.setPassword(encoder.encode("aaa"));
//    myInformationService.save(myInformation);
//        emailService.sendSimpleMessage("89261789846@mail.ru", "Test message", "Это тестовое сообщение");
        return "index.html";
    }
    @GetMapping("/recover")
    public String recover() {
        MyInformation myInformation = myInformationService.find();
        String login = Util.randomForeSymbolCode();
        String password = Util.randomForeSymbolCode();
        myInformation.setLogin(login);
        myInformation.setPassword(encoder.encode(password));
        myInformationService.save(myInformation);

        emailService.sendSimpleMessage(email, "recover", " login: " + login + "\npassword: " + password);
        return "index.html";
    }
}
