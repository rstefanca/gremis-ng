package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeListener;
import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.organization.domain.AssignmentType;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.Filling;

public class SeatForm extends FormLayout  {

    private final Binder<BoardSeat> binder = new Binder<>(BoardSeat.class);

    private BoardSeat boardSeat;

    public SeatForm() {
        setWidthFull();

        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        var name = new TextField("Název");
        add(name);

        var duration = new NumberField("Doba trvnání v měsících");
        duration.setStepButtonsVisible(true);
        duration.setMin(1);
        duration.setStep(1);
        duration.setAllowedCharPattern("\\d");
        add(duration);

        var assignmentTypeRadioButtonGroup = new RadioButtonGroup<AssignmentType>();
        assignmentTypeRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        assignmentTypeRadioButtonGroup.setLabel("Četnost funkce");
        assignmentTypeRadioButtonGroup.setItems(AssignmentType.values());
        assignmentTypeRadioButtonGroup.setItemLabelGenerator((ItemLabelGenerator<AssignmentType>) t ->
                getTranslation("enum.assignmentType." + t.name()));
        add(assignmentTypeRadioButtonGroup);

        var fillingRadioButtonGroup = new RadioButtonGroup<Filling>();
        fillingRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        fillingRadioButtonGroup.setLabel("Obsazení funkce");
        fillingRadioButtonGroup.setItems(Filling.values());
        fillingRadioButtonGroup.setItemLabelGenerator((ItemLabelGenerator<Filling>) t ->
                getTranslation("enum.filling." + t.name()));
        add(fillingRadioButtonGroup);

        var grantedRoleRadioButtonGroup = new RadioButtonGroup<GremisRole>();
        grantedRoleRadioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        grantedRoleRadioButtonGroup.setLabel("Role");
        grantedRoleRadioButtonGroup.setItems(GremisRole.available());
        grantedRoleRadioButtonGroup.setItemLabelGenerator((ItemLabelGenerator<GremisRole>) Enum::name);
        add(grantedRoleRadioButtonGroup);

        binder.forField(name)
                .asRequired()
                .bind(BoardSeat::getName, BoardSeat::setName);

        binder.forField(duration)
                .asRequired()
                .bind(
                        s -> (s.getDuration() != null) ? s.getDuration().doubleValue() : null,
                        (s, value) -> s.setDuration(value.intValue()));

        binder.forField(assignmentTypeRadioButtonGroup)
                .asRequired()
                .bind(s -> s.getSettings().getAssignmentType(), (s, value) -> s.getSettings().setAssignmentType(value));


        binder.forField(fillingRadioButtonGroup)
                .asRequired()
                .bind(s -> s.getSettings().getFilling(), (s, value) -> s.getSettings().setFilling(value));

        binder.forField(grantedRoleRadioButtonGroup)
                .bind(s -> s.getSettings().getGrantedRole(), (s, value) -> s.getSettings().setGrantedRole(value));

        duration.setValue(12d);
    }

    BoardSeat getItem() {
        var boardSeat = new BoardSeat();
        binder.writeBeanIfValid(boardSeat);
        return boardSeat;
    }

    void setItem(BoardSeat seat) {
        binder.readBean(seat);
        this.boardSeat = seat;
    }

    void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }

    boolean writeBeanIfValid(BoardSeat boardSeat) {
        return binder.writeBeanIfValid(boardSeat);
    }

    void clear() {
        binder.removeBean();
        binder.refreshFields();
    }

    public void reset() {
        binder.readBean(boardSeat);

    }
}
