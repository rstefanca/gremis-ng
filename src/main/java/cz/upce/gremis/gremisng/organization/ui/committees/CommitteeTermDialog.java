package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;
import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommitteeTermDialog extends Dialog {

    private static final String BOARD_TERM_COMBO_TEMPLATE =
            """
                    <div style="display: flex;">
                        <div>
                            ${item.description}
                            <div style=\\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\\">${item.from} - ${item.to}</div>
                        </div>
                    </div>""";

    private final Binder<CommitteeTerm> binder = new Binder<>(CommitteeTerm.class);

    private final ComboBox<TermComboItem> boardTerm = new ComboBox<>("Funkční období grémia");

    private final TextField name = new TextField("Název");

    private final DatePicker from = new DatePicker("Začátek");

    private final DatePicker to = new DatePicker("Konec");

    private CommitteeTerm committeeTerm;

    private BoardId boardId;

    private TermsDataProvider dataProvider;

    public CommitteeTermDialog(BoardTermRepository boardTermRepository) {
        dataProvider = new TermsDataProvider(boardTermRepository);

        configureForm();
        configureButtons();
        configureBinder();

        boardTerm.setItems(dataProvider);
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
        boardTerm.setItemLabelGenerator(TermComboItem::description);
        boardTerm.addValueChangeListener(e -> {
           if (from.getValue() == null && to.getValue() == null) {
               var selected = e.getValue();
               from.setValue(selected.start);
               to.setValue(selected.end);
           }
        });

        add(layout);
    }

    private Renderer<TermComboItem> createBoardTermRenderer() {
        return LitRenderer.<TermComboItem>of(BOARD_TERM_COMBO_TEMPLATE)
                .withProperty("description", TermComboItem::description)
                .withProperty("from", termDto -> termDto.start().toString())
                .withProperty("to", termDto1 -> termDto1.end().toString());
    }

    private void configureButtons() {
        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> fireEvent(new CancelEvent(this, null)));

        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> validateAndSave());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getFooter().add(cancelButton, saveButton);
    }

    private void validateAndSave() {
        if (binder.writeBeanIfValid(committeeTerm)) {
            fireEvent(new SaveEvent(this, committeeTerm));
        }
    }

    private void configureBinder() {
        binder.forField(boardTerm)
                .asRequired()
                .withConverter(TermComboItem::id, boardTermId -> {
                    if (boardTermId != null) {
                        return new TermComboItem(boardTermId);
                    }
                    return null;
                })
                .bind(CommitteeTerm::getBoardTermId, CommitteeTerm::setBoardTermId);

        binder.forField(name)
                .asRequired()
                .bind(CommitteeTerm::getName, CommitteeTerm::setName);

        binder.forField(from)
                .asRequired()
                .bind(CommitteeTerm::getStart, CommitteeTerm::setStart);

        binder.forField(to)
                .asRequired()
                .bind(CommitteeTerm::getEnd, CommitteeTerm::setEnd);

    }

    void setItem(CommitteeTerm committeeTerm) {
        this.committeeTerm = committeeTerm;
        binder.setBean(committeeTerm);
    }

    void setBoardId(BoardId boardId) {
        this.boardId = boardId;
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    @RequiredArgsConstructor
    private class TermsDataProvider implements CallbackDataProvider.FetchCallback<TermComboItem, String> {

        private final BoardTermRepository repository;

        @Override
        public Stream<TermComboItem> fetch(Query<TermComboItem, String> query) {
            var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
            var page = repository.pageTerms(boardId.value(), pageRequest);
            return page.stream().map(TermComboItem::new);
        }
    }

    /**
     * Helps binder to select item without calling database. Only compares ids.
     */
    @RequiredArgsConstructor
    private static class TermComboItem {

       private final Long id;
       private final String description;
       private final LocalDate start;
       private final LocalDate end;

       public TermComboItem(TermDto dto) {
           id = dto.id();
           description = dto.description();
           start = dto.start();
           end = dto.end();
       }

       public TermComboItem(Long id) {
           this.id = id;
           description = null;
           start = null;
           end = null;
       }

        public Long id() {
            return id;
        }

        public String description() {
            return description;
        }

        public LocalDate start() {
            return start;
        }

        public LocalDate end() {
            return end;
        }

        @Override
        public boolean equals(Object obj) {
           if (obj == null) {
               return false;
           }
            return this.id().equals(((TermComboItem)obj).id());
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
