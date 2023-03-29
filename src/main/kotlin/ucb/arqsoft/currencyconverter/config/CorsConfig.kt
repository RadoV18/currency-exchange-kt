package ucb.arqsoft.currencyconverter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfiguration = CorsConfiguration();
        corsConfiguration.allowCredentials = false;
        corsConfiguration.allowedOrigins = listOf("http://localhost:4200");
        corsConfiguration.allowedHeaders = listOf(
            "Origin",
            "Access-Control-Allow-Origin",
            "Content-Type", "Accept", "Authorization",
            "Origin, Accept", "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        );
        corsConfiguration.exposedHeaders = listOf(
            "Origin", "Content-Type",
            "Accept", "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        );
        corsConfiguration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE",
            "OPTIONS"
        )
        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource
            .registerCorsConfiguration("/**", corsConfiguration);
        return CorsFilter(urlBasedCorsConfigurationSource)
    }
}