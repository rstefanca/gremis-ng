package cz.upce.gremis.gremisng.persons.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.persons.domain.Gender;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonDetail;
import cz.upce.gremis.gremisng.persons.service.PersonService;
import cz.upce.gremis.gremisng.ui.MainLayout;


import java.util.List;


@Route(value = "person", layout = MainLayout.class)
public class PersonView extends VerticalLayout implements HasUrlParameter<Long> {

    private final PersonService personService;

    Dialog deleteDialog;

    private Long personId;
    private String fullName = "";

    private final Binder<PersonViewBean> binder = new Binder<>(PersonViewBean.class);

    public PersonView(PersonService personService) {
        this.personService = personService;

        var historyDialog = historyDialog();
        deleteDialog = confirmDeleteDialog();

        var degreeBeforeName = new TextField(getTranslation("personsView.form.degreeBeforeName"));
        var firstName = new TextField(getTranslation("common.firstName"));
        var lastName = new TextField(getTranslation("common.lastName"));
        var degreeAfterName = new TextField(getTranslation("personsView.form.degreeAfterName"));

        var genderComboBox = new ComboBox<Gender>(getTranslation("common.gender"));
        genderComboBox.setItems(Gender.values());
        genderComboBox.setItemLabelGenerator((ItemLabelGenerator<Gender>) gender ->
                getTranslation("common.gender." + gender.name()));

        var email = new EmailField("E-mail");
        var login = new TextField(getTranslation("common.loginName"));

        var employer = new TextField(getTranslation("personView.form.employer"));
        var workplace = new TextField(getTranslation("personView.form.workplace"));
        var job = new TextField(getTranslation("personView.form.job"));

        var note = new TextArea();
        note.setWidthFull();
        note.setMinHeight("150px");
        note.setLabel(getTranslation("common.note"));
        note.setClearButtonVisible(true);


        binder.forField(degreeBeforeName)
                .bind(PersonViewBean::getDegreeLeading, PersonViewBean::setDegreeLeading);

        binder.forField(firstName)
                .asRequired("Vypln")
                .bind(PersonViewBean::getFirstName, PersonViewBean::setFirstName);

        binder.forField(lastName)
                .asRequired("Vypln")
                .bind(PersonViewBean::getLastName, PersonViewBean::setLastName);

        binder.forField(degreeAfterName)
                .bind(PersonViewBean::getDegreeTrailing, PersonViewBean::setDegreeTrailing);

        binder.forField(genderComboBox)
                .bind(PersonViewBean::getGender, PersonViewBean::setGender);

        var loginBinder = binder.forField(login)
                .asRequired("Vypln")
                .withValidator(new LoginValidator())
                .bind(PersonViewBean::getLogin, PersonViewBean::setLogin);

        login.setReadOnly(true);


        var emailBinding = binder.forField(email)
                .asRequired(getTranslation("personView.validation.required.email"))
                .withValidator(new EmailValidator(getTranslation("validations.invalidEmail")))
                .bind(PersonViewBean::getEmail, PersonViewBean::setEmail);

        email.addValueChangeListener(e -> emailBinding.validate());

        binder.forField(employer)
                .bind(PersonViewBean::getEmployer, PersonViewBean::setEmployer);

        binder.forField(workplace)
                .bind(PersonViewBean::getWorkplace, PersonViewBean::setWorkplace);

        binder.forField(job)
                .bind(PersonViewBean::getJob, PersonViewBean::setJob);

        binder.forField(note)
                .bind(PersonViewBean::getNote, PersonViewBean::setNote);

        var spacer = new Div();

        var formLayout = new FormLayout();
        formLayout.add(
                degreeBeforeName,
                firstName,
                lastName,
                degreeAfterName,
                genderComboBox,
                spacer,
                email,
                login,
                employer,
                workplace,
                job,
                note);

        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new FormLayout.ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new FormLayout.ResponsiveStep("320px", 3),
                new FormLayout.ResponsiveStep("50px", 4));
        formLayout.setColspan(email, 2);
        formLayout.setColspan(login, 2);
        formLayout.setColspan(employer, 2);
        formLayout.setColspan(note, 2);
        formLayout.setColspan(spacer, 3);


        //menu bar
        var top = new HorizontalLayout();
        top.setWidthFull();

        Div menuDiv = createMenu(historyDialog, deleteDialog);

        var backIcon = VaadinIcon.ARROW_BACKWARD.create();
        var confirmed2 = new Span(new Span(getTranslation("grid.persons.state.active")),
                createIcon(VaadinIcon.CHECK));
        confirmed2.addClassName(LumoUtility.Margin.Left.LARGE);
        confirmed2.getElement().getThemeList().add("badge success");
        backIcon.setTooltipText("Na seznam uzivatelu");
        backIcon.addClickListener(e -> getUI().ifPresent(it -> it.navigate(PersonsView.class)));
        top.add(backIcon);
        top.add(confirmed2);
        top.add(menuDiv);


        //buttons row
        var formButtons = prepareFormButtons();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.add(new Span("Last login: 12.3.2023 12:23"));
        content.add(new Span("Autentizace: Lokalní"));
        var details = new Details("Podrobnosti");
        details.addContent(content);


        var profileLayout = new VerticalLayout(top, formLayout, formButtons, details,
                historyDialog, deleteDialog);

        var membership = new Tab();
        membership.add(new Text("Membership"));

        var tabs = new TabSheet();
        tabs.add(new Tab(VaadinIcon.USER.create(), new Span(getTranslation("personView.tabs.profile"))), profileLayout);
        tabs.add(new Tab(VaadinIcon.USERS.create(), new Span(getTranslation("personView.tabs.membership"))), membership);
        tabs.addSelectedChangeListener(e -> {
            if (e.getSelectedTab().equals(membership) && e.isInitialSelection()) {
                membership.add(new Text("Hello"));
            }
        });

        add(tabs);
    }

    private Div createMenu(Dialog historyDialog, Dialog deleteDialog) {
        var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        var menuDiv = new Div(menu);
        menuDiv.setWidthFull();

        var invite = menu.addItem(new Icon(VaadinIcon.ENVELOPE_OPEN_O));
        invite.add("Zaslat pozvanku");

        var activate = menu.addItem("Aktivovat");
        var deactivate = menu.addItem("Deaktivovat");
        activate.setEnabled(false);
        var history = menu.addItem(new Icon(VaadinIcon.OPEN_BOOK));
        history.add("Historie");
        history.addClickListener(e -> historyDialog.open());

        var deleteItem = menu.addItem(new Icon(VaadinIcon.TRASH));
        deleteItem.add(getTranslation("buttons.remove"));
        deleteItem.addClickListener(e -> this.openDeleteDialog());
        return menuDiv;
    }

    private Component prepareFormButtons() {
        var saveButton = new Button(getTranslation("buttons.save"));
        saveButton.addClickListener(this::savePerson);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var resetButton = new Button("Reset");
        resetButton.addClickListener(this::reset);

        binder.addStatusChangeListener(e -> {
            Binder<?> b = e.getBinder();
            saveButton.setEnabled(b.hasChanges());
            resetButton.setEnabled(b.hasChanges());
        });

        return new HorizontalLayout(saveButton, resetButton);
    }

    private void reset(ClickEvent<Button> buttonClickEvent) {
        personService.loadPerson(personId, this::bindPerson);
    }

    private void bindPerson(Person person) {
        var personViewBean = new PersonViewBean(person);
        binder.readBean(personViewBean);
        fullName = person.getFullName();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long id) {
        personService.loadPerson(id, this::bindPerson);
        personId = id;
    }

    private Dialog historyDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Historie osoby");

        VerticalLayout dialogLayout = createDialogLayout(dialog);
        dialog.add(dialogLayout);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        return dialog;
    }

    private VerticalLayout createDialogLayout(Dialog dialog) {
        var grid = new Grid<PersonDetail>();
        grid.addColumn(PersonDetail::getFullName).setHeader(getTranslation("common.fullName"));


        grid.addColumn(PersonDetail::getEmployer).setHeader(getTranslation("personView.form.employer"));
        grid.addColumn(PersonDetail::getWorkplace).setHeader(getTranslation("personView.form.workplace"));
        grid.addColumn(PersonDetail::getJob).setHeader(getTranslation("personView.form.job"));

        grid.setItems(List.of(
                PersonDetail.builder()
                        .firstName("Ales")
                        .lastName("Novy")
                        .employer("Upce")
                        .workplace("Rektorat")
                        .build(),
                PersonDetail.builder()
                        .firstName("Ales")
                        .lastName("Novy")
                        .employer("Upce")
                        .workplace("Sekretariat")
                        .build()
        ));

        Button closeButton = new Button(getTranslation("buttons.close"), e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(grid, closeButton);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(Alignment.STRETCH);
        dialogLayout.getStyle().set("min-width", "500px")
                .set("max-width", "100%").set("height", "100%");
        dialogLayout.setAlignSelf(Alignment.END, closeButton);

        return dialogLayout;
    }

    private void savePerson(ClickEvent<Button> e) {
        if (binder.isValid()) {
            personService.updatePerson(personId, person -> {
                var personViewBean = new PersonViewBean(person);
                boolean valid = binder.writeBeanIfValid(personViewBean);
                if (valid) {
                    Notification.show("Saved");
                    e.getSource().setEnabled(false);
                }
            });
        }
    }

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

    private Dialog confirmDeleteDialog() {
        Dialog dialog = new Dialog();

        dialog.add(getTranslation("personView.confirmDeleteDialog.question"));

        Button deleteButton = new Button(getTranslation("buttons.remove"), (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button(getTranslation("buttons.cancel"), (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);

        return dialog;
    }

    private void openDeleteDialog() {
        deleteDialog.open();
        deleteDialog.setHeaderTitle(
                getTranslation("personView.confirmDeleteDialog.header", fullName));
    }

    private class LoginValidator implements Validator<String> {

        @Override
        public ValidationResult apply(String value, ValueContext context) {

            var loginExists = personService.checkLoginExists(personId, value);
            if (loginExists) {
                return ValidationResult.create(getTranslation("validations.loginExists"), ErrorLevel.ERROR);
            } else {
                return ValidationResult.ok();
            }
        }
    }


}
