package restaurante.promocao;

import java.time.LocalDateTime;
import java.time.LocalTime;
import restaurante.modelo.Categoria;
import restaurante.modelo.Comanda;
import restaurante.modelo.ItemPedido;

public class HappyHour implements Promocao {

    private double percentual;
    private LocalTime inicio;
    private LocalTime fim;

    public HappyHour(double percentual, LocalTime inicio, LocalTime fim) {
        validarPercentual(percentual);
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("O horário de início deve ser anterior ao horário de término.");
        }
        this.percentual = percentual;
        this.inicio = inicio;
        this.fim = fim;
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
    public LocalTime getInicio() {
        return this.inicio;
    }
    public LocalTime getFim() {
        return this.fim;
    }

    @Override
    public String getDescricao() {
        return "Happy hour (" + this.percentual + "% em bebidas, " + this.inicio + " às " + this.fim + ")";
    }

    @Override
    public double calcularDesconto(ItemPedido item, Comanda comanda, LocalDateTime momento) {
        LocalTime hora = momento.toLocalTime();
        boolean dentroDoHorario = !hora.isBefore(this.inicio) && hora.isBefore(this.fim);

        if (dentroDoHorario && item.getItem().getCategoria() == Categoria.BEBIDA) {
            return item.calcularSubtotal() * this.percentual / 100.0;
        }
        return 0;
    }
}
