package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;

import java.util.function.Consumer;


class BoardTermDialog extends Dialog {
    private static final Consumer<BoardTerm> NULL_CONSUMER = (term) -> {};

    private final BoardTermForm form;
    private Consumer<BoardTerm> consumer = NULL_CONSUMER;

    public BoardTermDialog() {
        setHeaderTitle("Nové funkční odbobí");
        form = new BoardTermForm();

        add(form);

        Button saveButton = new Button(getTranslation("buttons.save"), (e) -> {
            if (form.isValid()) {
                var boardTerm = form.getItem();
                consumer.accept(boardTerm);
                close();
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");
        getFooter().add(saveButton);

        getFooter().add(new Button(getTranslation("buttons.cancel"), e -> {
            form.clear();
            this.close();
        }));
    }

    void setItem(BoardTerm boardTerm) {
        form.setItem(boardTerm);
    }

    void setOnSave(Consumer<BoardTerm> consumer) {
        this.consumer = consumer;
    }
}
