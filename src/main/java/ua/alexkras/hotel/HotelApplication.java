package ua.alexkras.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import ua.alexkras.hotel.entity.MySqlStrings;
import java.sql.*;


@SpringBootApplication
public class HotelApplication implements WebMvcConfigurer {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/static/", "classpath:/public/"
	};


	public static void main(String[] args) {
		SpringApplication.run(HotelApplication.class, args);

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", MySqlStrings.user, MySqlStrings.password);
			 PreparedStatement createDB = conn.prepareStatement(MySqlStrings.sqlCreateDatabaseIfNotExists);
			 PreparedStatement createUserTable = conn.prepareStatement(MySqlStrings.sqlCreateUserTableIfNotExists)
			) {
			createDB.execute();
			createUserTable.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to establish MqSQL connection and create database: "+MySqlStrings.databaseName);
		}

	}

	@Bean
	public LocaleResolver localeResolver() {
		return new CookieLocaleResolver();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
}
