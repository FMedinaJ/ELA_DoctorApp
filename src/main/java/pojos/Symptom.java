package pojos;

import java.util.List;
import java.util.Objects;

public class Symptom {
    private Integer id;
    private String information;
    private List<MedicalInformation> reports;

    public Symptom(Integer id, String information, List<MedicalInformation> reports) {
        this.id = id;
        this.information = information;
        this.reports = reports;

    }
    public Integer getId() {
        return id;
    }
    public String getInformation() {
        return information;
    }
    public List<MedicalInformation> getReports() {
        return reports;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setReports(List<MedicalInformation> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Symptom symptom = (Symptom) o;
        return Objects.equals(id, symptom.id) && Objects.equals(information, symptom.information) && Objects.equals(reports, symptom.reports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, information, reports);
    }

    @Override
    public String toString() {
        return "Symptom{" +
                "id=" + id +
                ", information='" + information + '\'' +
                ", reports=" + reports +
                '}';
    }
}

