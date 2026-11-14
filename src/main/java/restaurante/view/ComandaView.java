package restaurante.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import restaurante.modelo.Comanda;
import restaurante.modelo.FormatadorMoeda;
import restaurante.modelo.ItemCardapio;
import restaurante.modelo.ItemPedido;
import restaurante.sistema.Restaurante;

@Route("comanda")
public class ComandaView extends VerticalLayout {

    private Restaurante restaurante = Restaurante.getInstancia();

    private IntegerField campoMesa = new IntegerField("Número da mesa");
    private ComboBox<Comanda> campoComanda = new ComboBox<>("Comanda aberta");
    private ComboBox<ItemCardapio> campoItem = new ComboBox<>("Item do cardápio");
    private IntegerField campoQuantidade = new IntegerField("Quantidade");
    private Grid<ItemPedido> grid = new Grid<>(ItemPedido.class, false);
    private Span resumo = new Span("Abra ou selecione uma comanda.");

    public ComandaView() {
        campoMesa.setMin(1);
        campoMesa.setStepButtonsVisible(true);

        campoComanda.addValueChangeListener(e -> atualizarItensDaComanda());

        campoItem.setItemLabelGenerator(item ->
                item.getNome() + " - " + FormatadorMoeda.formatar(item.calcularPreco()));
        campoItem.setWidth("320px");

        campoQuantidade.setMin(1);
        campoQuantidade.setValue(1);
        campoQuantidade.setStepButtonsVisible(true);

        grid.addColumn(ip -> ip.getItem().getNome()).setHeader("Item");
        grid.addColumn(ip -> ip.getItem().getCategoria().getDescricao()).setHeader("Categoria");
        grid.addColumn(ItemPedido::getQuantidade).setHeader("Qtd.");
        grid.addColumn(ip -> FormatadorMoeda.formatar(ip.getPrecoUnitario())).setHeader("Preço unit.");
        grid.addColumn(ip -> FormatadorMoeda.formatar(ip.calcularSubtotal())).setHeader("Subtotal");
        grid.setWidthFull();

        Button btnAbrir = new Button("Abrir comanda", e -> abrirComanda());
        Button btnAdicionar = new Button("Adicionar item", e -> adicionarItem());
        Button btnRemover = new Button("Remover item selecionado", e -> removerItem());

        HorizontalLayout linhaAbrir = new HorizontalLayout(campoMesa, btnAbrir, campoComanda);
        linhaAbrir.setAlignItems(Alignment.END);

        HorizontalLayout linhaItem = new HorizontalLayout(campoItem, campoQuantidade, btnAdicionar, btnRemover);
        linhaItem.setAlignItems(Alignment.END);

        setWidthFull();
        add(new MenuNavegacao(), new H1("Comanda"), linhaAbrir, linhaItem, grid, resumo);

        atualizarListas();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        atualizarListas();
    }

    // ---------- ações ----------

    private void abrirComanda() {
        if (campoMesa.getValue() == null) {
            Notification.show("Informe o número da mesa.");
            return;
        }
        try {
            Comanda nova = restaurante.abrirComanda(campoMesa.getValue());
            atualizarListas();
            campoComanda.setValue(nova);
            campoMesa.clear();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    private void adicionarItem() {
        Comanda comanda = campoComanda.getValue();
        ItemCardapio item = campoItem.getValue();
        Integer quantidade = campoQuantidade.getValue();

        if (comanda == null || item == null || quantidade == null) {
            Notification.show("Selecione a comanda, o item e a quantidade.");
            return;
        }
        try {
            comanda.adicionarItem(item, quantidade);
            campoQuantidade.setValue(1);
            atualizarItensDaComanda();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    private void removerItem() {
        Comanda comanda = campoComanda.getValue();
        ItemPedido selecionado = grid.asSingleSelect().getValue();

        if (comanda == null || selecionado == null) {
            Notification.show("Selecione um item na tabela.");
            return;
        }
        try {
            comanda.removerItem(selecionado.getItem());
            atualizarItensDaComanda();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Notification.show(ex.getMessage());
        }
    }

    // ---------- atualização da tela ----------

    private void atualizarListas() {
        campoComanda.setItems(new ArrayList<>(restaurante.listarComandasAbertas()));
        campoItem.setItems(new ArrayList<>(restaurante.listarCardapio()));
        atualizarItensDaComanda();
    }

    private void atualizarItensDaComanda() {
        Comanda comanda = campoComanda.getValue();
        if (comanda == null || !comanda.isAberta()) {
            grid.setItems(new ArrayList<ItemPedido>());
            resumo.setText("Abra ou selecione uma comanda.");
            return;
        }
        grid.setItems(new ArrayList<>(comanda.getItens()));
        resumo.setText("Subtotal: " + FormatadorMoeda.formatar(comanda.calcularSubtotal())
                + " | Total estimado (promoções e taxa de serviço): "
                + FormatadorMoeda.formatar(comanda.calcularTotalParcial(restaurante.listarPromocoes())));
    }
}
