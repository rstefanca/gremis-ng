package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.service.CommitteeAggregateService;
import cz.upce.gremis.gremisng.ui.MainLayout;


@Route(value = "/committee", layout = MainLayout.class)
public class CommitteeDetailView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private final CommitteeAggregateService service;

    private final CommitteeTermDialog committeeTermDialog;


    private final Span titleSpan = new Span();

    private final TabSheet tabSheet = new TabSheet();

    private final CommitteeForm committeeForm = new CommitteeForm();

    private Committee committee;

    public CommitteeDetailView(CommitteeAggregateService service,
                               CommitteeTermDialog committeeTermDialog) {
        this.service = service;
        this.committeeTermDialog = committeeTermDialog;

        setWidthFull();
        setHeightFull();

        configureTop();
        configureTabs();
        addFormTab();
        addTermsTab();
        addSeatsTab();
    }

    private void configureTop() {
        var top = new HorizontalLayout();
        top.setWidthFull();
        top.setAlignItems(Alignment.END);

        titleSpan.setClassName(LumoUtility.FontSize.LARGE);
        titleSpan.setWidthFull();

        top.add(titleSpan);
        add(top);
    }

    private void configureTabs() {
        tabSheet.setWidthFull();
        /*tabSheet.add("Detail", new Div());
        tabSheet.add("Všechna funkční období", new Div());
        tabSheet.add("Funkce", new Div());*/
        add(tabSheet);
    }

    private void addFormTab() {
        var formLayout = new VerticalLayout();
        formLayout.add(committeeForm);
        tabSheet.add("Detail", formLayout);
    }

    private void addTermsTab() {
        var content = new LazyTerms();
        var tab = tabSheet.add("Všechna funkční období", content);
        content.setTab(tab);
    }

    private void addSeatsTab() {
        tabSheet.add("Funkce", new Div());
    }

    @Override
    public String getPageTitle() {
        return committee.getName();
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        committee = service.getCommittee(new CommitteeId(parameter));
        committeeForm.setItem(committee);
        updateTitle();
    }

    private void updateTitle() {
        titleSpan.setText(committee.getName());
    }



    private class LazyTerms extends Div {

        private final Grid<TermDto> grid = new Grid<>();

        private Tab tab;

        LazyTerms() {
            addAttachListener(attachEvent -> {
                var verticalLayout = new VerticalLayout();
                verticalLayout.add(new Button("Pridat term", e -> openDialog()));
                configureGrid(verticalLayout);
                add(verticalLayout);

                refresh();
            });
        }

        private void configureGrid(HasComponents parent) {
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addColumn(TermDto::description).setHeader("Popis");
            grid.addColumn(TermDto::start).setHeader("Začátek");
            grid.addColumn(TermDto::end).setHeader("Konec");
            parent.add(grid);
        }

        private void refresh() {
            var terms = service.getTermDtos(new CommitteeId(committee.getId()));
            grid.setItems(terms);
        }

        private void openDialog() {

            committeeTermDialog.setItem(new CommitteeTerm());
            committeeTermDialog.setBoardId(new BoardId(committee.getBoard().getId()));
            committeeTermDialog.open();

            committeeTermDialog.addSaveListener(e -> {
                saveTerm(e.getTerm());
                committeeTermDialog.close();
                committeeTermDialog.setItem(null);
                tabSheet.setSelectedTab(this.tab);
            });
            committeeTermDialog.addCloseListener(e -> committeeTermDialog.close());
        }

        private void saveTerm(CommitteeTerm term) {
            if (term.isNew()) {
                term.setCommittee(committee);
            }
            service.saveTerm(term);
            refresh();
            Notification.show("Saved");

        }

        public void setTab(Tab tab) {
            this.tab = tab;
        }
    }
}
