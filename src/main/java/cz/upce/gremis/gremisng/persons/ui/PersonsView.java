package cz.upce.gremis.gremisng.persons.ui;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonState;
import cz.upce.gremis.gremisng.ui.MainLayout;
import jakarta.annotation.security.PermitAll;


@Route(value = "users", layout = MainLayout.class)
@PermitAll
public class PersonsView extends VerticalLayout {

    static final String PERSON_TEMPLATE_EXPRESSION = "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
            + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
            + "    <span> ${item.fullName} </span>"
            + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
            + "      ${item.email}" + "    </span>"
            + "  </vaadin-vertical-layout>"
            + "</vaadin-horizontal-layout>";

    private final PersonDataProvider personDataProvider;

    private PersonFilter personFilter = new PersonFilter();

    public PersonsView(PersonDataProvider personDataProvider) {
        this.personDataProvider = personDataProvider;

        ConfigurableFilterDataProvider<Person, Void, PersonFilter> filterDataProvider = personDataProvider
                .withConfigurableFilter();

        //search
        TextField searchField = prepareSearchField();
        searchField.addValueChangeListener(e -> {
            personFilter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(personFilter);
        });
        searchField.setClearButtonVisible(true);

        var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        var menuDiv = new Div(menu);
        menuDiv.setWidthFull();
        var top = new HorizontalLayout(searchField, menuDiv);
        top.setWidthFull();
        var addPersonMenuItem = menu.addItem(new Icon(VaadinIcon.PLUS));
        addPersonMenuItem.add("Nová osoba");

        //grid
        Grid<Person> grid = prepareGrid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItems(filterDataProvider);
        grid.addItemClickListener(e ->
                grid.getUI().ifPresent(ui -> ui.navigate(PersonView.class, e.getItem().getId())));


        VerticalLayout layout = new VerticalLayout(top, grid);
        layout.setPadding(false);
        add(layout);
    }

    private TextField prepareSearchField() {
        var searchField = new TextField();
        searchField.setWidth("25%");
        searchField.setPlaceholder(getTranslation("common.search"));
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        return searchField;
    }

    private Grid<Person> prepareGrid() {
        var grid = new Grid<Person>();
        grid.setWidthFull();


        grid.addColumn(new ComponentRenderer<>(person -> new Avatar(person.getFirstName() + " " + person.getLastName())))
                .setWidth("5em").setFlexGrow(0).setResizable(false);


        //full name and username
        grid.addColumn(createPersonRenderer()).setHeader(getTranslation("common.fullName"));

        //email
        grid.addColumn(Person::getEmail).setHeader("E-mail");

        //state
        grid.addColumn(createStatusComponentRenderer()).setHeader(getTranslation("grid.persons.state"));

        //authentication method
        grid.addColumn(Person::getAuthenticationMethod).setHeader(getTranslation("common.authentication"));
        grid.setMinHeight("200px");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        return grid;
    }

    private static Renderer<Person> createPersonRenderer() {
        return LitRenderer.<Person>of(PERSON_TEMPLATE_EXPRESSION)
                .withProperty("fullName", Person::getFullName)
                .withProperty("email", Person::getLogin);
    }

    private static ComponentRenderer<Span, Person> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    private static final SerializableBiConsumer<Span, Person> statusComponentUpdater = (span, person) -> {
        PersonState state = person.getState();
        String text = span.getTranslation("grid.persons.state." + state.name().toLowerCase());
        span.setText(text);
        switch (state) {
            case ACTIVE, BLOCKED -> {
                String theme = String.format("badge %s", state == PersonState.ACTIVE ? "success" : "error");
                span.getElement().setAttribute("theme", theme);

            }
            default -> span.getElement().setAttribute("theme", "badge");
        }
    };
}
