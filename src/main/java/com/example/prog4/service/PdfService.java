package com.example.prog4.service;

import com.example.prog4.config.CompanyConf;
import com.example.prog4.model.Employee;
import com.example.prog4.model.exception.InternalServerErrorException;
import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

@Component
public class PdfService {

  private static final String EMPLOYEE_HTML_TEMPLATE = "employee_card";

  public byte[] generatePdf(Employee employee,
                            CompanyConf companyConf, String template) {
    ITextRenderer renderer = new ITextRenderer();
    loadStyle(renderer, employee, companyConf, template);
    renderer.layout();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      renderer.createPDF(outputStream);
    } catch (DocumentException e) {
      throw new InternalServerErrorException("Pdf generation ended with exception :" + e);
    }
    return outputStream.toByteArray();
  }

  private void loadStyle(ITextRenderer renderer, Employee employee,
                         CompanyConf companyConf, String template) {
    renderer.setDocumentFromString(parseCardTemplateToString(employee, companyConf, template));

  }

  private String parseCardTemplateToString(
      Employee employee, CompanyConf companyConf, String template) {
    TemplateEngine templateEngine = configureTemplate();
    Context context = configureContext(employee, companyConf);
    return templateEngine.process(template, context);
  }

  private Context configureContext(Employee employee, CompanyConf companyConf) {
    Context context = new Context();
    context.setVariable("employee", employee);
    context.setVariable("companyConf", companyConf);
    return context;
  }

  private TemplateEngine configureTemplate() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("/pdf/");
    templateResolver.setSuffix(".html");
    templateResolver.setCharacterEncoding("UTF-8");
    templateResolver.setTemplateMode(HTML);
    templateResolver.setOrder(1);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    return templateEngine;
  }

  public byte[] getPdfCard(Employee employee) {
    return generatePdf(employee, new CompanyConf(), EMPLOYEE_HTML_TEMPLATE);
  }
}
