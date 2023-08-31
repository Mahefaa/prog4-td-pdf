package com.example.prog4.controller;

import com.example.prog4.controller.mapper.EmployeeMapper;
import com.example.prog4.controller.validator.EmployeeValidator;
import com.example.prog4.model.Employee;
import com.example.prog4.model.EmployeeFilter;
import com.example.prog4.model.enums.AgeCriteria;
import com.example.prog4.service.CSVUtils;
import com.example.prog4.service.EmployeeService;
import com.example.prog4.service.PdfService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Controller
@AllArgsConstructor
@RequestMapping("/server/employee")
public class EmployeeController {
    private EmployeeMapper employeeMapper;
    private EmployeeValidator employeeValidator;
    private EmployeeService employeeService;
    private final PdfService pdfService;

    @GetMapping("/list/csv")
    public ResponseEntity<byte[]> getCsv(HttpSession session) {
        EmployeeFilter filters = (EmployeeFilter) session.getAttribute("employeeFiltersSession");
        List<Employee> data = employeeService.getAll(filters).stream().map(employeeMapper::toView).toList();

        String csv = CSVUtils.convertToCSV(data);
        byte[] bytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "employees.csv");
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, OK);
    }

    @GetMapping("/list/filters/clear")
    public String clearFilters(HttpSession session) {
        session.removeAttribute("employeeFilters");
        return "redirect:/employee/list";
    }

    @PostMapping("/createOrUpdate")
    public String saveOne(@ModelAttribute Employee employee) {
        employeeValidator.validate(employee);
        com.example.prog4.repository.entity.Employee domain = employeeMapper.toDomain(employee);
        employeeService.saveOne(domain);
        return "redirect:/employee/list";
    }
    @GetMapping(value = "/show/{eId}/toPdf", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> toPdf(
        @PathVariable("eId") String employeeId,
        @RequestParam(value = "ageCriteria", required = false, defaultValue = "BIRTHDAY")
        AgeCriteria ageCriteria,
        @RequestParam(value = "birthday_min_interval", required = false) Long birthdayMinInterval) {
        Employee employee = employeeMapper.toView(
            employeeService.getOne(employeeId),
            ageCriteria,
            birthdayMinInterval
        );
        byte[] pdfCardAsBytes = pdfService.getPdfCard(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "employee.csv");
        headers.setContentLength(pdfCardAsBytes.length);
        return new ResponseEntity<>(pdfCardAsBytes, headers, OK);
    }

}
