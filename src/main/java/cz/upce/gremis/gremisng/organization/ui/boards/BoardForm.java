package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.ComponentEventBus;
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
import jakarta.annotation.security.RolesAllowed;

// TODO - use events
class BoardForm extends FormLayout {

    private final TextField name;
    private final TextField abbreviation;
    private final RadioButtonGroup<TermType> termType;
    private final NumberField minMemberCount;
    private final CheckboxGroup<BoardOption> options;

    private Binder<Board> binder = new Binder<>(Board.class);

    private Board board;

    public BoardForm() {
        setWidthFull();

        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        name = new TextField("Název grémia");
        name.setValueChangeMode(ValueChangeMode.LAZY);
        name.setAutocapitalize(Autocapitalize.SENTENCES);
        name.setValueChangeMode(ValueChangeMode.LAZY);
        name.setClearButtonVisible(true);
        name.setMinLength(3);

        abbreviation = new TextField("Zkratka");
        abbreviation.setValueChangeMode(ValueChangeMode.LAZY);
        abbreviation.setAutocapitalize(Autocapitalize.CHARACTERS);
        abbreviation.setClearButtonVisible(true);

        termType = new RadioButtonGroup<>();
        termType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        termType.setLabel("Typ funčního obdbobí");
        termType.setItems(TermType.values());
        termType.setItemLabelGenerator((ItemLabelGenerator<TermType>) gender ->
                getTranslation("enum.termType." + gender.name()));

        minMemberCount = new NumberField("Min. počet členů");
        minMemberCount.setStepButtonsVisible(true);
        minMemberCount.setMin(1);
        minMemberCount.setStep(1);

        options = new CheckboxGroup<>();
        options.setLabel("Možnosti");
        options.setItems(BoardOption.values());
        options.setItemLabelGenerator(
                (ItemLabelGenerator<BoardOption>) option -> getTranslation("enum.boardOption." + option.name()));
        options.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        add(name, 2);
        add(abbreviation, minMemberCount, termType,  options);
        configureBinder();
    }

    private void configureBinder() {
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
    }

    Board getBoard() {
        return board;
    }

    boolean writeBeanIfValid() {
       return binder.writeBeanIfValid(board);
    }

    boolean writeBeanIfValid(Board board) {
        return binder.writeBeanIfValid(board);
    }


    boolean isValid() {
        return binder.isValid();
    }

    void showErrors() {
        binder.validate();
    }

    void setBoard(Board board) {
        this.board = board;
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

    void reset() {
        binder.readBean(board);
    }

    void clear() {
        binder.removeBean();
    }
}
