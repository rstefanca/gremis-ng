package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeListener;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardOption;
import cz.upce.gremis.gremisng.organization.domain.TermType;

// TODO - use events
class BoardForm extends FormLayout {

    private Binder<Board> binder = new Binder<>(Board.class);

    public BoardForm() {

        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));


        var name = new TextField("Nazev gremia");
        name.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        name.setAutocapitalize(Autocapitalize.SENTENCES);

        var abbreviation = new TextField("Zkratka");
        abbreviation.setAutocapitalize(Autocapitalize.CHARACTERS);
        abbreviation.setMaxWidth("12em");

        var termType = new RadioButtonGroup<TermType>();
        termType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        termType.setLabel("Typ funkcniho obdbobi");
        termType.setItems(TermType.values());
        termType.setItemLabelGenerator((ItemLabelGenerator<TermType>) gender ->
                getTranslation("enum.termType." + gender.name()));

        var minMemberCount = new NumberField("Min. pocet clenu");
        minMemberCount.setStepButtonsVisible(true);
        minMemberCount.setMaxWidth("12em");

        minMemberCount.setMin(1);
        minMemberCount.setStep(1);

        var options = new CheckboxGroup<BoardOption>();

        options.setLabel("Moznosti");
        options.setItems(BoardOption.values());
        options.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        add(name, abbreviation, termType, minMemberCount, options);

        binder.forField(name)
                .asRequired()
                .bind(Board::getName, Board::setName);

        binder.forField(abbreviation)
                .asRequired()
                .bind(Board::getAbbreviation, Board::setAbbreviation);

        binder.forField(termType)
                .asRequired()
                .bind(Board::getTermType, Board::setTermType);

        binder.forField(minMemberCount)
                .asRequired()
                .bind(
                        b -> Integer.valueOf(b.getMinMemberCount()).doubleValue(),
                        (b, value) -> b.setMinMemberCount(value.intValue()));

        binder.forField(options)
                        .bind(Board::getOptions, Board::setOptions);

        minMemberCount.setValue(1d);

    }

    Board getBoard() {
        var board = new Board();
        binder.writeBeanIfValid(board);
        return board;
    }

    boolean isValid() {
        return binder.isValid();
    }

    void showErrors() {
        binder.validate();
    }

    void setBoard(Board board) {
        binder.readBean(board);
    }

    void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }

    public void setReadOnly(boolean readOnly) {
        this.getChildren().forEach(component -> {
            if (component instanceof HasValue<?,?> hasValueComponent) {
                hasValueComponent.setReadOnly(readOnly);
            }
        });
    }
}
