package cloud.cirrusup;

import cloud.cirrusup.filter.AuthenticationFilter;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Service entry point.
 */
@SpringBootApplication
@EnableSwagger2
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    GenericApplicationContext server;

    public static void main(String[] args) {
        LOG.info("Starting application... ");
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean filterAuthenticationBean() {

        LOG.info("Registering Authentication Filter");

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setOrder(0);
        registrationBean.setFilter(server.getBean(AuthenticationFilter.class));
        registrationBean.setUrlPatterns(ImmutableList.of("/hello"));

        LOG.info("Authentication filter registered.");

        return registrationBean;
    }

    @Bean
    public Docket createDocketForSwaggerIntegration() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getPaths()).build()
                .useDefaultResponseMessages(false);
    }

    public Predicate<String> getPaths() {

        return or(regex("/hello*"));
    }

}
