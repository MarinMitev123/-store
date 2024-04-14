package bg.tu.sofia.store;

import bg.tu.sofia.store.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("bg.tu.sofia.store.dao")
public class Main implements CommandLineRunner {

    @Resource private FilesStorageService filesStorageService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        filesStorageService.init();
    }
}