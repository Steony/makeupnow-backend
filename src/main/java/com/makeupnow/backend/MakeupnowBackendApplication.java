package com.makeupnow.backend;

import com.makeupnow.backend.model.mysql.Provider;
import com.makeupnow.backend.model.mysql.Role;
import com.makeupnow.backend.model.mysql.Service;
import com.makeupnow.backend.repository.mysql.ProviderRepository;
import com.makeupnow.backend.repository.mysql.ServiceRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MakeupnowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MakeupnowBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ProviderRepository providerRepository, ServiceRepository serviceRepository) {
	    return args -> {
	        // Création d’un Provider fictif
	        Provider provider = new Provider();
	        provider.setFirstname("Sophie");
	        provider.setLastname("MUA");
	        provider.setEmail("sophie@makeupnow.com");
	        provider.setPassword("secure123");
	        provider.setAddress("Dakar");
	        provider.setPhoneNumber("770000000");
	        provider.setActive(true);
	        provider.setRole(Role.PROVIDER);
	        //provider.setCertificationStatus(CertificationStatus.APPROVED);

	        providerRepository.save(provider);

	        // Création d’un Service associé
	        Service s = new Service();
	        s.setTitle("Maquillage Mariée");
	        s.setDescription("Maquillage pour cérémonie avec tenue longue");
	        s.setDuration(90);
	        s.setPrice(120.0);
	        s.setProvider(provider);

	        serviceRepository.save(s);

	        System.out.println("✅ Provider + Service sauvegardés !");
	    };
	}
}
