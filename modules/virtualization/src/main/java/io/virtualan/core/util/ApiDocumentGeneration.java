package io.virtualan.core.util;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Component;

@Component
public class ApiDocumentGeneration {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName(contactName);
        contact.setEmail( contactEmail);
        contact.setUrl(contactUrl);
        return new OpenAPI()
            .info(new Info()
                .title(apiTitle)
                .description(apiTitle)
                .termsOfService("https://www.virtualan.io/contact-us.html")
                .contact(contact)
                .license(new License().name("Apache 2.0").url("https://www.virtualan.io")));
    }

    @Value("${virtualan.api.contact.name:Elan Thangamani}")
    private String contactName;

    @Value("${virtualan.api.contact.url:http://www.virtualan.io}")
    private String contactUrl;

    @Value("${virtualan.api.contact.email:elan.thangamani@virtualan.io}")
    private String contactEmail;

    @Value("${virtualan.api.title:Virtualan API Catalog}")
    private String apiTitle;

}
