package restaurante.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import restaurante.modelo.Comanda;
import restaurante.modelo.FormatadorMoeda;
import restaurante.promocao.Promocao;
import restaurante.sistema.Restaurante;

@Route("fechamento")
public class FechamentoView extends VerticalLayout {

    private Restaurante restaurante = Restaurante.getInstancia();

    private ComboBox<Comanda> campoComanda = new ComboBox<>("Comanda aberta");
    private Pre recibo = new Pre();
    private VerticalLayout blocoPromocoes = new VerticalLayout();

    private DatePicker campoData = new DatePicker("Data");
    private Grid<Comanda> gridHistorico = new Grid<>(Comanda.class, false);
    private Span faturamento = new Span();

    public FechamentoView() {
        Button btnFechar = new Button("Fechar conta", e -> fecharConta());
        HorizontalLayout linhaFechar = new HorizontalLayout(campoComanda, btnFechar);
        linhaFechar.setAlignItems(Alignment.END);

        blocoPromocoes.setPadding(false);
        blocoPromocoes.setSpacing(false);

        campoData.setValue(LocalDate.now());
        Button btnConsultar = new Button("Consultar", e -> consultarHistorico());
        HorizontalLayout linhaHistorico = new HorizontalLayout(campoData, btnConsultar);
        linhaHistorico.setAlignItems(Alignment.END);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        gridHistorico.addColumn(Comanda::getNumero).setHeader("Comanda");
        gridHistorico.addColumn(Comanda::getNumeroMesa).setHeader("Mesa");
        gridHistorico.addColumn(c -> c.getFechamento().format(formato)).setHeader("Fechamento");
        gridHistorico.addColumn(c -> FormatadorMoeda.formatar(c.getTotal())).setHeader("Total");
        gridHistorico.setWidthFull();
        gridHistorico.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                recibo.setText(e.getValue().getRecibo());
            }
        });

        setWidthFull();
        add(new MenuNavegacao(), new H1("Fechamento de conta"),
                linhaFechar, recibo,
                new H2("Promoções ativas"), blocoPromocoes,
                new H2("Histórico de comandas fechadas"), linhaHistorico, gridHistorico, faturamento);

        atualizarTela();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        atualizarTela();
    }

    // ---------- ações ----------

    private void fecharConta() {
        Comanda comanda = campoComanda.getValue();
        if (comanda == null) {
            Notification.show("Selecione a comanda que será fechada.");
            return;
        }
        try {
            restaurante.fecharComanda(comanda);
            recibo.setText(comanda.getRecibo());
            atualizarTela();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    private void consultarHistorico() {
        LocalDate data = campoData.getValue();
        if (data == null) {
            Notification.show("Informe a data da consulta.");
            return;
        }
        gridHistorico.setItems(new ArrayList<>(restaurante.consultarHistorico(data)));
        faturamento.setText("Faturamento do dia: " + FormatadorMoeda.formatar(restaurante.calcularFaturamento(data)));
    }

    // ---------- atualização da tela ----------

    private void atualizarTela() {
        campoComanda.setItems(new ArrayList<>(restaurante.listarComandasAbertas()));

        blocoPromocoes.removeAll();
        for (Promocao promocao : restaurante.listarPromocoes()) {
            blocoPromocoes.add(new Span("• " + promocao.getDescricao()));
        }
        consultarHistorico();
    }
}
