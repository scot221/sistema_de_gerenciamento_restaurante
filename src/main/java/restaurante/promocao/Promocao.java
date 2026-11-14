package restaurante.promocao;

import java.time.LocalDateTime;
import restaurante.modelo.Comanda;
import restaurante.modelo.ItemPedido;

public interface Promocao {

    String getDescricao();

    // retorna o valor (em reais) do desconto concedido sobre o item; zero se não se aplica
    double calcularDesconto(ItemPedido item, Comanda comanda, LocalDateTime momento);

    default boolean concedeDesconto(ItemPedido item, Comanda comanda, LocalDateTime momento) {
        return calcularDesconto(item, comanda, momento) > 0;
    }
}
