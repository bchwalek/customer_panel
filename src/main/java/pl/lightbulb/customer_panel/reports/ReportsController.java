package pl.lightbulb.customer_panel.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.lightbulb.customer_panel.UserService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ReportsController {

    private final ReportsService reportsService;
    private final UserService userService;

    @GetMapping("/addreports/{id}")
    public String addReportsForm (@PathVariable Long id, Model model){
        model.addAttribute("reportsForAdmin", reportsService.userReports(userService.findById(id).get()));
        model.addAttribute("customerId", id);
        model.addAttribute("report", new Reports());
        return "add_reports";
    }

    @PostMapping("/reportupload")
    public String upolad (@RequestParam("document") MultipartFile multipartFile,
                          @RequestParam("reportName")String reportName,
                          @RequestParam("userId")String userId
    ) throws IOException {


        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));


        Reports report = new Reports();
        report.setName(fileName);
        report.setReportName(reportName);
        report.setContent(multipartFile.getBytes());
        report.setSize(multipartFile.getSize());
        report.setUploadTime(new Date());
        report.setUser(userService.findById(Long.parseLong(userId)).get());

        reportsService.add(report);

        return "redirect:/showcustomer";
    }

    @GetMapping("/downlad/{id}")
    public void dwonFile(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
        Optional<Reports> resault = reportsService.findById(id);
        if(!resault.isPresent()){
            throw new Exception("Nie ma takiego pliku");
        }
        Reports reports = resault.get();
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Dispostion";
        String headerValue = "attachment, filename" + reports.getReportName();
        response.setHeader(headerKey, headerValue);

        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(reports.getContent());
        servletOutputStream.close();
    }

    @GetMapping("/deletereport/{id}")
    public String deleteReport(@PathVariable("id") Long id){
        reportsService.delete(reportsService.findById(id).get());
        return "redirect:/showcustomer";
    }
}
