package krylov.psychology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import krylov.psychology.service.DayServiceImpl;
import krylov.psychology.service.DayTimeServiceImpl;
import krylov.psychology.service.ProductServiceImpl;
import krylov.psychology.service.TherapyServiceImpl;
import krylov.psychology.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DBRider
class PsychologyApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DayServiceImpl dayService;
    @Autowired
    private DayTimeServiceImpl dayTimeService;
    @Autowired
    private TherapyServiceImpl therapyService;
    @Autowired
    private ProductServiceImpl productService;
    private long longData = Util.dateTomorrow(new Date()).getTime();
    private String headerBearer;

//    @RegisterExtension
//    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
//            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
//            .withPerMethodLifecycle(false);

    @BeforeAll
    @DBUnit(schema = "public")
    @DataSet("fillingDB.yml")
    public void setHeaderBearer() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(post("/admin/login")
                        .param("login", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                ).andReturn().getResponse();
        Cookie[] cookies = response.getCookies();
        for (var i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("auth_token")) {
                this.headerBearer = cookies[i].getValue();
            }
        }

        Date tomorrow = Util.dateTomorrow(new Date());
        String longDay = String.valueOf(tomorrow.getTime());
        MockHttpServletResponse response1 =
                mockMvc.perform(post("/admin/post_create_new_day")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                        .param("dataTime", longDay)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();
        assertThat(response1.getStatus()).isEqualTo(302);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void rootPage() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(get("/"))
                        .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Nested
    @DisplayName("Tests for admin page and login")
    class AdminTests {
        @Test
        public void adminLogin() throws Exception {
            MockHttpServletResponse response1 =
                    mockMvc.perform(post("/admin/login")
                            .param("login", "admin")
                            .param("password", "admin")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andReturn().getResponse();
            assertThat(response1.getStatus()).isEqualTo(200);
        }

        @Test
        public void adminPageWithoutAuthorization() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(post("/admin")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);
        }

        @Test
        public void adminPage() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }
    @Nested
    @DisplayName("Tests for the products")
    class ProductTest {
        @Test
        public void createNewProductGet() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/create_new_product")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }

//        @Test
//        public void createNewProductPost() throws Exception {
//            MockHttpServletResponse response =
//                    mockMvc.perform(post("/admin/post_create_new_product")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                            .param("productName", "Therapy")
//                            .param("cost", "3000")
//                            .param("duration", "03:00:00")
//                            .param("description", "какое-то описание")
//                            .param("actual", "true")
//                            .param("priority", "6")
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    ).andReturn().getResponse();
//            assertThat(response.getStatus()).isEqualTo(302);
//        }

        @Test
        public void createNewProductPostNotCorrectData() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(post("/admin/post_create_new_product")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                            .param("productName", "")
                            .param("cost", "3000000")
                            .param("duration", "02:30:00")
                            .param("description", "какое-то описание")
                            .param("actual", "true")
                            .param("priority", "6")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("must not be blank",
                    "must be less than or equal to 9999");
        }

        @Test
        public void adminAllProducts() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/admin_all_products")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }

        @Test
        public void adminOneProducts() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/show_one_product/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Терапия 2");
        }

        @Test
        public void adminUpdateProductGet() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/update_product/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Терапия 2");
        }

//        @Test
//        public void adminUpdateProductPost() throws Exception {
//            MockHttpServletResponse response =
//                    mockMvc.perform(post("/admin/update_product/2")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                            .param("productName", "NewProduct")
//                            .param("cost", "3000000")
//                            .param("duration", "04:30")
//                            .param("description", "какое-то описание")
//                            .param("actual", "true")
//                            .param("priority", "6")
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                            .accept(MediaType.APPLICATION_JSON)
//                    ).andReturn().getResponse();
//            assertThat(response.getStatus()).isEqualTo(302);
//
//            MockHttpServletResponse response2 =
//                    mockMvc.perform(get("/admin/admin_all_products")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                    ).andReturn().getResponse();
//            assertThat(response2.getContentAsString()).doesNotContain("Терапия 2");
//            assertThat(response2.getContentAsString()).contains("New product");
//        }

        @Test
        public void adminActivateProduct() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/active_product/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

            MockHttpServletResponse response2 =
                    mockMvc.perform(get("/admin/admin_all_products")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response2.getContentAsString()).contains("false");
        }

        @Test
        public void adminDeleteProduct() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/delete_product/1")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

            MockHttpServletResponse response2 =
                    mockMvc.perform(get("/admin/admin_all_products")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response2.getContentAsString()).doesNotContain("Терапия 1");
            assertThat(response2.getContentAsString()).contains("Терапия 2");
        }
    }
    @Nested
    @DisplayName("Tests for default time")
    class DefaultTimeTest {
        @Test
        public void showAllDefaultTime() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/default_time")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("6:00", "7:00", "8:00", "9:00");
            assertThat(response.getContentAsString()).doesNotContain("10:00");
        }
        @Test
        public void newDefaultTimeGet() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/new_default_time")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }
//        @Test
//        public void newDefaultTimePost() throws Exception {
//            System.out.println("Don't work newDefaultTimePostTest");
//            MockHttpServletResponse response =
//                    mockMvc.perform(post("/admin/new_default_time")
//                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                                    .param("time",  "17:00")
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                    ).andReturn().getResponse();
//            assertThat(response.getStatus()).isEqualTo(200);
//
//
//            MockHttpServletResponse response1 =
//                    mockMvc.perform(get("/admin/default_time")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                    ).andReturn().getResponse();
//            assertThat(response1.getStatus()).isEqualTo(200);
//            assertThat(response1.getContentAsString()).contains("17:00");
//        }
        @Test
        public void deleteDefaultTime() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/delete_default_time/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

            MockHttpServletResponse response1 =
                    mockMvc.perform(get("/admin/default_time")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response1.getStatus()).isEqualTo(200);
            assertThat(response1.getContentAsString()).doesNotContain("6:00");

        }
    }
    @Nested
    @DisplayName("Create Month")
    class SomeTests {
        @Test
        public void activeNextMonthGet() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/active_new_month")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }
        @Test
        public void activeNewMonthPost() throws Exception {
            Date today = new Date();
            Integer month = today.getMonth();
            Integer year = today.getYear();
            String str = "10=10&20=20";

            MockHttpServletResponse response =
                    mockMvc.perform(post("/admin/active_new_month")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                            .param("month", Integer.toString(month))
                            .param("year", Integer.toString(year))
                            .content(str)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);


        }
    }
    @Nested
    @DisplayName("Tests for Days")
    class DayTest {
        @Test
        public void showAllWorkingDays() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/all_days")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }
        @Test
        public void showExistedDay() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/admin_one_day/" + longData)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("05:00", "06:00");
        }
        @Test
        public void showNoyExistedDay() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/admin_one_day/0")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Открыть запись на данный день");
        }
        @Test
        public void deleteDayTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/delete_day/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

            MockHttpServletResponse response1 =
                    mockMvc.perform(get("/admin/all_days")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response1.getStatus()).isEqualTo(200);
            assertThat(response1.getContentAsString()).doesNotContain("05:00", "06:00");
        }
//        @Test
//        public void createNewDay() throws Exception {
//            Date date = new Date();
//            int year = date.getYear();
//            int month = date.getMonth();
//            int day1 = date.getDay() + 1;
//            Date tomorrow = new Date(year, month, day1 + 1);
//            String longDay = String.valueOf(tomorrow.getTime());
//            MockHttpServletResponse response1 =
//                    mockMvc.perform(post("/admin/post_create_new_day")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                            .param("dataTime", longDay)
//                            .accept(MediaType.APPLICATION_JSON)
//                    ).andReturn().getResponse();
//            assertThat(response1.getStatus()).isEqualTo(302);
//        }
        @Test
        public void deactivateTime() throws Exception {
            Date tomorrow = Util.dateTomorrow(new Date());
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/daytime_active/5/" + tomorrow.getTime())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

            MockHttpServletResponse response1 =
                    mockMvc.perform(get("/admin/admin_one_day/" + tomorrow.getTime())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response1.getStatus()).isEqualTo(200);
            assertThat(response1.getContentAsString()).contains("false");
        }
    }
    @Nested
    @DisplayName("root tests")
    class RootTest {
        @Test
        public void rootPage() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Услуги");
        }
        @Test
        public void showAllProduct() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/all_therapies")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Терапия 2");
            assertThat(response.getContentAsString()).doesNotContain("Терапия 1");
        }
        @Test
        public void showOneProductTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/product/2")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Терапия 2");
        }
        @Test
        public void recordingOnTherapyTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/record/2")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Пожалуйста выберите день и время", "05:00");
            assertThat(response.getContentAsString()).doesNotContain("Выбранное время уже заняли");
        }
        @Test
        public void recordingOnTherapyNextWeekTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/record/2?week=5")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Пожалуйста выберите день и время");
            assertThat(response.getContentAsString()).doesNotContain("05:00, Выбранное время уже заняли");
        }
        @Test
        public void recordingOnTherapyTimeIsOccupyTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/record/2?timeIsOccupy=true")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Выбранное время уже заняли");
        }
        @Test
        public void recordingOnTherapy2Test() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/record/2/2")
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Терапия 2", "06:00",
                    "Пожалуйста введите контактные данные");
            assertThat(response.getContentAsString()).doesNotContain("Вы ввели не тот код", "05:00");
        }
//        @Test
//        public void recordingOnTherapy3Test() throws Exception {
//            MockHttpServletResponse response =
//                    mockMvc.perform(get("/confirm/2/2")
//                            .param("name", "admin")
//                            .param("email", "admin@admin.com")
//                            .param("phoneNumber", "412342")
//                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                            .accept(MediaType.APPLICATION_JSON)
//                    ).andReturn().getResponse();
//            assertThat(response.getStatus()).isEqualTo(200);
//        }
        @Test
        public void oneTherapyTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/one_therapy/2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("admin", "Терапия 3", "06:00");
            assertThat(response.getContentAsString()).doesNotContain("05:00");
        }
        @Test
        public void deleteTherapyTest() throws Exception {
            String str = "y=y&e=e&s=s";
            MockHttpServletResponse response =
                    mockMvc.perform(post("/admin/delete_therapy/1/")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                            .content(str)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(302);

//            MockHttpServletResponse response1 =
//                    mockMvc.perform(get("/admin/one_therapy/2")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                    ).andReturn().getResponse();
//            assertThat(response1.getStatus()).isEqualTo(200);
//            assertThat(response1.getContentAsString()).contains("stgestrge");

//            boolean therapyIsDeleted = false;
//            try {
//                mockMvc.perform(get("/admin/one_therapy/2")
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                ).andReturn().getResponse();
//            } catch (Exception e) {
//                therapyIsDeleted = true;
//            }
//            assertThat(therapyIsDeleted).isTrue();
        }
        @Test
        public void transferTherapyChooseDayTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/transfer_therapy/1/")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("05:00");
        }
//        @Test
//        public void transferTherapyConfirmTest() throws Exception {
//            MockHttpServletResponse response =
//                    mockMvc.perform(get("/admin/transfer/1/5")
//                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
//                    ).andReturn().getResponse();
//            assertThat(response.getStatus()).isEqualTo(302);
//            assertThat(response.getContentAsString()).doesNotContain("терапия");
//        }
        @Test
        public void getMyInfoTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/info")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("Обновить информацию");
        }
        @Test
        public void updateMyInfoTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(post("/admin/update_info")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                            .param("shortInformation", "Информация обо мне")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
        }
        @Test
        public void updateLoginTest() throws Exception {
            MockHttpServletResponse response =
                    mockMvc.perform(get("/admin/update_login_info")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                    ).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("логин");
        }
    }

}
