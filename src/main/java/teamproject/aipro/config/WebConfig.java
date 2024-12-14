package teamproject.aipro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedOrigins("https://www.mjuaipro.site", "https://www.sinjjanggu.site", "https://www.hongkikii.site", "https://ai-pro-fe.vercel.app")
			.allowedHeaders("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowCredentials(true);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
