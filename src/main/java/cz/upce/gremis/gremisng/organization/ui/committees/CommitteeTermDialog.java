package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxLazyDataView;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.BoardId;
import cz.upce.gremis.gremisng.organization.domain.BoardTermRepository;
import cz.upce.gremis.gremisng.organization.domain.CommitteeTerm;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import cz.upce.gremis.gremisng.time.BusinessTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class CommitteeTermDialog extends Dialog {

    private static final String BOARD_TERM_COMBO_TEMPLATE =
            """
                    <div style="display: flex;">
                        <div>
                            ${item.description}
                            <div style=\\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\\">${item.from} - ${item.to}</div>
                        </div>
                    </div>""";

    private final Binder<CommitteeTerm> binder = new Binder<>(CommitteeTerm.class);

    private final ComboBox<TermDto> boardTerm = new ComboBox<>("Funkční období grémia");

    private final BusinessTime businessTime;

    private final TextField name = new TextField("Název");

    private final DatePicker from = new DatePicker("Začátek");

    private final DatePicker to = new DatePicker("Konec");

    private final ComboBoxLazyDataView<TermDto> dataView;

    private CommitteeTerm committeeTerm;

    private BoardId boardId;


    public CommitteeTermDialog(BoardTermRepository boardTermRepository, BusinessTime businessTime) {
        this.businessTime = businessTime;
        configureForm();
        configureButtons();
        configureBinder();

        var dataProvider = new TermsDataProvider(boardTermRepository);
        //TODO check public <C> ComboBoxLazyDataView<TItem> setItemsWithFilterConverter(
        dataView = boardTerm.setItems(dataProvider);
        dataView.setIdentifierProvider(TermDto::id);
    }

    private void configureForm() {
        var layout = new FormLayout();
        layout.setWidthFull();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        layout.add(boardTerm, 2);
        layout.add(name, 2);
        layout.add(from, to);

        boardTerm.setRenderer(createBoardTermRenderer());
        boardTerm.setItemLabelGenerator(TermDto::description);
        boardTerm.addValueChangeListener(e -> {
            if (from.getValue() == null && to.getValue() == null) {
                var selected = e.getValue();
                from.setValue(selected.start());
                to.setValue(selected.end());
            }
        });

        name.setClearButtonVisible(true);

        add(layout);

        addCloseListener(e -> reset());
    }

    private Renderer<TermDto> createBoardTermRenderer() {
        return LitRenderer.<TermDto>of(BOARD_TERM_COMBO_TEMPLATE)
                .withProperty("description", TermDto::description)
                .withProperty("from", termDto -> termDto.start().toString())
                .withProperty("to", termDto1 -> termDto1.end().toString());
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> validateAndSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> fireEvent(new CancelEvent(this, null)));


        getFooter().add(saveButton, cancelButton);
    }

    private void validateAndSave() {
        if (binder.writeBeanIfValid(committeeTerm)) {
            fireEvent(new SaveEvent(this, committeeTerm));
        }
    }

    private void configureBinder() {
        binder.forField(boardTerm)
                .asRequired()
                .withConverter(TermDto::id, boardTermId -> {
                    if (boardTermId != null) {
                        return dataView.getItems()
                                .filter(it -> it.id().equals(boardTermId))
                                .findFirst()
                                .orElse(null);
                    }
                    return null;
                })
                .bind(CommitteeTerm::getBoardTermId, CommitteeTerm::setBoardTermId);

        binder.forField(name)
                .asRequired()
                .bind(CommitteeTerm::getName, CommitteeTerm::setName);

        var fromBinder = binder.forField(from)
                .asRequired()
                .withValidator(date -> !isIntervalSet() || date.isBefore(to.getValue()),
                        "Neplatny začátek období"
                )
                .withValidator(date -> !date.isBefore(getSelectedBoardTerm().getStart()), "check start")
                .bind(CommitteeTerm::getStart, CommitteeTerm::setStart);


        var toBinder = binder.forField(to)
                .asRequired()
                .withValidator(toDate -> isIntervalSet() && toDate.isAfter(from.getValue()),
                        "Neplatný konec období"
                )
                .withValidator(date -> !date.isAfter(getSelectedBoardTerm().end()), "check end")
                .bind(CommitteeTerm::getEnd, CommitteeTerm::setEnd);

        boardTerm.addValueChangeListener(e -> {
            fromBinder.validate();
            toBinder.validate();
        });
    }

    private boolean isIntervalSet() {
        return from.getValue() != null && to.getValue() != null;
    }

    private TermDto getSelectedBoardTerm() {
        return boardTerm.getValue();
    }

    void setItem(CommitteeTerm committeeTerm) {
        this.committeeTerm = committeeTerm;
        binder.setBean(committeeTerm);
    }

    void setBoardId(BoardId boardId) {
        this.boardId = boardId;
    }

    private void reset() {
        boardTerm.setValue(null);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    @RequiredArgsConstructor
    private class TermsDataProvider implements CallbackDataProvider.FetchCallback<TermDto, String> {

        private final BoardTermRepository repository;

        @Override
        public Stream<TermDto> fetch(Query<TermDto, String> query) {
            var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
            var page = repository.pageTerms(boardId.value(), pageRequest);
            return page.stream()
                    .peek(dto -> {
                        var value = boardTerm.getValue();
                        if (value == null) {
                            var now = businessTime.getDate();
                            dto.containsDate(now);
                            boardTerm.setValue(dto);
                        }
                    });
        }
    }

    public static sealed abstract class CommitteeTermDialogEvent extends ComponentEvent<CommitteeTermDialog> {

        private final CommitteeTerm term;

        public CommitteeTermDialogEvent(CommitteeTermDialog source, CommitteeTerm term) {
            super(source, false);
            this.term = term;
        }

        public CommitteeTerm getTerm() {
            return term;
        }
    }

    public static final class SaveEvent extends CommitteeTermDialogEvent {

        public SaveEvent(CommitteeTermDialog source, CommitteeTerm term) {
            super(source, term);
        }
    }

    public static final class CancelEvent extends CommitteeTermDialogEvent {

        public CancelEvent(CommitteeTermDialog source, CommitteeTerm term) {
            super(source, term);
        }
    }
}
