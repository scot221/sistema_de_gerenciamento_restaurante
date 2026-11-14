package restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import restaurante.sistema.Restaurante;

@SpringBootApplication
public class RestauranteApplication {

    public static void main(String[] args) {
        Restaurante.getInstancia().carregarDadosExemplo();
        SpringApplication.run(RestauranteApplication.class, args);
    }
}
