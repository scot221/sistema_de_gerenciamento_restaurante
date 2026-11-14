package restaurante.promocao;

import java.time.LocalDateTime;
import restaurante.modelo.Categoria;
import restaurante.modelo.Comanda;
import restaurante.modelo.ItemPedido;

public class DescontoSobremesa implements Promocao {

    private double percentual;

    public DescontoSobremesa(double percentual) {
        validarPercentual(percentual);
        this.percentual = percentual;
    }

    private static void validarPercentual(double percentual) {
        if (percentual <= 0 || percentual > 100) {
            throw new IllegalArgumentException("O percentual deve estar entre 0 e 100.");
        }
    }

    // setters
    public void setPercentual(double percentual) {
        validarPercentual(percentual);
        this.percentual = percentual;
    }

    // getters
    public double getPercentual() {
        return this.percentual;
    }

    @Override
    public String getDescricao() {
        return "Sobremesa com " + this.percentual + "% de desconto na compra de prato principal";
    }

    @Override
    public double calcularDesconto(ItemPedido item, Comanda comanda, LocalDateTime momento) {
        boolean ehSobremesa = item.getItem().getCategoria() == Categoria.SOBREMESA;

        if (ehSobremesa && comanda.temCategoria(Categoria.PRATO_PRINCIPAL)) {
            return item.calcularSubtotal() * this.percentual / 100.0;
        }
        return 0;
    }
}
