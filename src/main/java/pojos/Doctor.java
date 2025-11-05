package pojos;

import java.sql.DatabaseMetaData;
import java.util.Date;
import java.util.Objects;

public class Doctor {
    private Integer id;
    private String name;
    private String surname;
    private String DNI;
    private java.sql.Date birthDate;
    private String Sex;
    public String email;

    public Doctor(Integer id, String name, String surname, String DNI, java.sql.Date birthDate, String gender, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.DNI = DNI;
        this.birthDate = birthDate;
        this.Sex = gender;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDNI() {
        return DNI;
    }

    public void String ( String DNI) {
        this.DNI = DNI;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(java.sql.Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String gender) {
        this.Sex = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return id == doctor.id && DNI == doctor.DNI && Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(birthDate, doctor.birthDate) && Objects.equals(Sex, doctor.Sex) && Objects.equals(email, doctor.email);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, DNI, birthDate, Sex, email);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", DNI=" + DNI +
                ", birthDate=" + birthDate +
                ", gender='" + Sex + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
