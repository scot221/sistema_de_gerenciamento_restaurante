package restaurante.view;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MenuNavegacao extends HorizontalLayout {

    public MenuNavegacao() {
        add(
                new RouterLink("Cardápio", CardapioView.class),
                new RouterLink("Comanda", ComandaView.class),
                new RouterLink("Fechamento de conta", FechamentoView.class)
        );
        setSpacing(true);
    }
}
