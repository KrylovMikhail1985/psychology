package krylov.psychology;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

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
    @Value("${adminName}")
    private String adminName;
    private String headerBearer;
    @BeforeAll
    @DBUnit(schema = "public")
    @DataSet("fillingDB.yml")
    public void setHeaderBearer() throws Exception {
        MockHttpServletResponse response =
         mockMvc.perform(post("/admin/login")
                .param("login", adminName)
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        Cookie[] cookies = response.getCookies();
        for (var i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("auth_token")) {
                this.headerBearer = cookies[i].getValue();
            }
        }
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
    @Test
    public void adminLogin() throws Exception {
        MockHttpServletResponse response1 =
                mockMvc.perform(post("/admin/login")
                                .param("login", adminName)
                                .param("password", "password")
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
    @Test
    public void createNewProduct() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(get("/admin/create_new_product")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                ).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }
    @Test
    public void createNewProductPost() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(post("/admin/post_create_new_product")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                        .param("productName", "Therapy")
                        .param("cost", "3000")
                        .param("duration", "2,5 часа")
                        .param("description", "какое-то описание")
                        .param("actual", "true")
                        .param("priority", "6")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(302);
    }
    @Test
    public void createNewProductPostNotCorrectData() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(post("/admin/post_create_new_product")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                        .param("productName", "")
                        .param("cost", "3000000")
                        .param("duration", "2,5 часа")
                        .param("description", "какое-то описание")
                        .param("actual", "true")
                        .param("priority", "6")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("must not be blank", "must be less than or equal to 9999");
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
    @Test
    public void adminUpdateProductPost() throws Exception {
        MockHttpServletResponse response =
                mockMvc.perform(post("/admin/update_product/2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                        .param("productName", "New product")
                        .param("cost", "3500")
                        .param("duration", "2,5 часа")
                        .param("description", "какое-то описание")
                        .param("actual", "true")
                        .param("priority", "6")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(302);

        MockHttpServletResponse response2 =
                mockMvc.perform(get("/admin/admin_all_products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + headerBearer)
                ).andReturn().getResponse();
        assertThat(response2.getContentAsString()).doesNotContain("Терапия 2");
        assertThat(response2.getContentAsString()).contains("New product");
    }
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
