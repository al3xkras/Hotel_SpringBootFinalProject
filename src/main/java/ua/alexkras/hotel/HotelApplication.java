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
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.model.MySqlStrings;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.UserType;
import java.sql.*;
import java.time.LocalDate;


@SpringBootApplication
public class HotelApplication implements WebMvcConfigurer {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", "classpath:/resources/",
			"classpath:/static/", "classpath:/public/"
	};

	public static void main(String[] args) {

		//Create database if not exists before starting Spring Boot application
		try (Connection conn = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password);
			 PreparedStatement createDB = conn.prepareStatement(MySqlStrings.sqlCreateDatabaseIfNotExists);
			 PreparedStatement createUserTable = conn.prepareStatement(MySqlStrings.sqlCreateUserTableIfNotExists)
			) {
			createDB.execute();
			createUserTable.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to establish MqSQL connection and create database: "+MySqlStrings.databaseName);
		}

		try {
			UserDAO.addUser(
					new User("Admin", "0",
							"Admin1", "password1",
							"+404-23-4567890",
							LocalDate.parse("2002-03-07")
							, "Male", UserType.ADMIN)
			);

			UserDAO.addUser(
					new User("MyName", "MySurname",
							"Admin2", "password2",
							"+404-12-3456789",
							LocalDate.parse("2002-03-07")
							, "Male", UserType.ADMIN)
			);
		}catch (SQLIntegrityConstraintViolationException ignored){

		} catch (SQLException e){
			e.printStackTrace();
			System.out.println("Cannot add Admin accounts to hotel's database.");
		}

		SpringApplication.run(HotelApplication.class, args);
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
