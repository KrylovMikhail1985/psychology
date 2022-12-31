package krylov.psychology.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Entity
public class MyInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    @Column(name = "login")
    private String login;
    @NotBlank
    @Column(name = "password")
    private String password;
    @Lob
    @Column(name = "short_information")
    private String shortInformation;

    public MyInformation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShortInformation() {
        return shortInformation;
    }

    public void setShortInformation(String shortInformation) {
        this.shortInformation = shortInformation;
    }
}
