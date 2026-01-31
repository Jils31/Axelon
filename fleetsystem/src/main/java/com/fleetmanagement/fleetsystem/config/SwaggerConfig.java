package com.fleetmanagement.fleetsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI fleetManagementAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");


        Info info = new Info()
                .title("Fleet Management System API")
                .version("1.0.0")
                .description("RESTful API for managing fleet vehicles, drivers, trips, maintenance, and analytics");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}