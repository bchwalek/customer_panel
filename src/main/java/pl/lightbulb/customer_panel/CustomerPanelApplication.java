package pl.lightbulb.customer_panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CustomerPanelApplication  {

//    extends SpringBootServletInitializer
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(CustomerPanelApplication.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(CustomerPanelApplication.class, args);
    }

}
