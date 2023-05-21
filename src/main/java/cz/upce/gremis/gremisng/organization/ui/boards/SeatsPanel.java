package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;

import java.util.ArrayList;
import java.util.Collection;

public class SeatsPanel extends VerticalLayout {

    private ArrayList<SeatForm> forms = new ArrayList<>();

    public SeatsPanel() {

        var firstForm = new SeatForm();

        forms.add(firstForm);
        forms.add(new SeatForm());

        forms.forEach(this::add);


        setSpacing(true);



        //firstForm.getElement().attachShadow();
    }

    Collection<BoardSeat> getItems() {
        return forms.stream().map(SeatForm::getItem).toList();
    }
}
