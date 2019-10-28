package io.virtualan.core.util;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import io.virtualan.controller.VirtualServiceController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class ApiDocumentGeneration {

    @Autowired
    private VirtualServiceController virtualServiceController;

    @Bean
    public Docket customImplementation(ServletContext servletContext) {
        final Docket docket = new Docket(DocumentationType.SWAGGER_2);
        final ApiSelectorBuilder selector = docket.select();
        for (final Map.Entry<String, Class> virtualServices : virtualServiceController
                .getVirtualServiceInfo().findVirtualServices().entrySet()) {
            selector.apis(RequestHandlerSelectors
                    .basePackage(virtualServices.getValue().getPackage().getName()));
        }
        selector.build().directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(java.time.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
        return docket;
    }


    @Value("${virtualan.api.contact.name:Elan Thangamani}")
    private String contactName;

    @Value("${virtualan.api.contact.url:http://www.virtualan.io}")
    private String contactUrl;

    @Value("${virtualan.api.contact.email:elans3.java@gmail.com}")
    private String contactEmail;

    @Value("${virtualan.api.title:Virtualan API Catalog}")
    private String apiTitle;

    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(apiTitle).description("Virtualan API Catalog")
                .license("Apache-2.0").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("").version("1.0.0")
                .contact(new Contact(contactName, contactUrl, contactEmail)).build();
    }


}
