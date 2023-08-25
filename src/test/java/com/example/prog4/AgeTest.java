package com.example.prog4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class AgeTest {
  @Test
  void age() {
    LocalDate birthday = LocalDate.of(2000, 2, 2);
    LocalDate case1 = LocalDate.of(2023, 1, 1);
    LocalDate case2 = LocalDate.of(2023, 2, 2);
    LocalDate case3 = LocalDate.of(2023, 2, 1);
    System.out.println(ChronoUnit.YEARS.between(birthday, case1));
    System.out.println(ChronoUnit.YEARS.between(birthday, case2));
    System.out.println(ChronoUnit.YEARS.between(birthday, case3));
  }
}
