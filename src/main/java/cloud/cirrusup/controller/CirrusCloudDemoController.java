package cloud.cirrusup.controller;

import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@ImportResource(locations = "classpath:spring-config/applicationContext.xml")
@PropertySource("classpath:properties/config-${spring.profiles.active}.properties")
public class CirrusCloudDemoController {

    @Value("${default.text}")
    private String message;

    private static final Logger LOG = LoggerFactory.getLogger(CirrusCloudDemoController.class);

    @GetMapping("/hello")
    @ApiImplicitParam(name = "Authorization", value = "Key for authentication", allowMultiple = false, paramType = "header", required = true)
    public String hello() {

        LOG.info("GET /hello request.");
        return "Hello! " + message;
    }
}
