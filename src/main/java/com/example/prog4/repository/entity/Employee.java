package com.example.prog4.repository.entity;

import com.example.prog4.model.enums.AgeCriteria;
import com.example.prog4.model.exception.BadRequestException;
import com.example.prog4.repository.entity.enums.Csp;
import com.example.prog4.repository.entity.enums.Sex;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.annotations.ColumnTransformer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static com.example.prog4.model.enums.AgeCriteria.BIRTHDAY;
import static com.example.prog4.model.enums.AgeCriteria.CUSTOM_DELAY;
import static com.example.prog4.model.enums.AgeCriteria.YEAR_ONLY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.temporal.ChronoUnit.YEARS;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Table(name = "\"employee\"")
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;
    private String cin;
    private String cnaps;
    private String image;
    private String address;
    private String lastName;
    private String firstName;
    private String personalEmail;
    private String professionalEmail;
    private String registrationNumber;

    private LocalDate birthDate;
    private LocalDate entranceDate;
    private LocalDate departureDate;

    private Integer childrenNumber;
    private BigDecimal monthlySalary;
    @Transient
    private int age;
    public int getAge(){
        return Math.toIntExact(YEARS.between(birthDate, LocalDate.now()));
    }

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "CAST(sex AS varchar)", write = "CAST(? AS sex)")
    private Sex sex;
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "CAST(csp AS varchar)", write = "CAST(? AS csp)")
    private Csp csp;

    @ManyToMany
    @JoinTable(
            name = "have_position",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private List<Position> positions;
    @OneToMany
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private List<Phone> phones;
    public int getAgeFromCriteriaAndInterval(AgeCriteria ageCriteria, Long birthdayMinInterval){
        if (ageCriteria == BIRTHDAY){
            return getAge();
        }
        if (ageCriteria == YEAR_ONLY){
            Year birthYear = Year.from(getBirthDate());
            return Year.now().getValue() - birthYear.getValue();
        }
        if (ageCriteria == CUSTOM_DELAY){
            if (birthdayMinInterval == null){
                throw new BadRequestException("When CUSTOM_DELAY is set, ageCriteria must be also set !");
            }
            LocalDate adjustedBirthday = getBirthDate().minusDays(birthdayMinInterval);
            return Math.toIntExact(YEARS.between(adjustedBirthday, LocalDate.now()));
        }
        throw new NotImplementedException("Not implemented parameter");
    }
}