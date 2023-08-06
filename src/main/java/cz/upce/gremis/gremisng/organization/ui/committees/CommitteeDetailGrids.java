package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommitteeDetailGrids extends VerticalLayout {

    private final CommitteeAssignmentsGrid committeeAssignmentsGrid;

    private final WorkingGroupsGrid workingGroupsGrid;

    private final CommitteeTermsComboBox committeeTermsComboBox;

    private final Details assignmentsDetails;

    private final Details workingGroupDetails;

    private final HashSet<String> needsReload = new HashSet<>();

    public CommitteeDetailGrids(CommitteeAssignmentsGrid committeeAssignmentsGrid,
                                WorkingGroupsGrid workingGroupsGrid,
                                CommitteeTermsComboBox committeeTermsComboBox) {
        this.committeeAssignmentsGrid = committeeAssignmentsGrid;
        this.workingGroupsGrid = workingGroupsGrid;
        this.committeeTermsComboBox = committeeTermsComboBox;

        setWidthFull();

        Div termsComboDiv = new Div();
        termsComboDiv.setWidth("33%");

        termsComboDiv.add(committeeTermsComboBox);
        committeeTermsComboBox.setWidthFull();
        add(termsComboDiv);


        assignmentsDetails = new Details("Osoby ve funkcích", committeeAssignmentsGrid);
        assignmentsDetails.setWidthFull();
        assignmentsDetails.addOpenedChangeListener(e -> {
            if (e.isOpened() && needsReload.contains("assignments")) {
                committeeAssignmentsGrid.refresh();
                needsReload.remove("assignments");
            }
        });

        workingGroupDetails = new Details("Pracovní skupiny", workingGroupsGrid);
        workingGroupDetails.setWidthFull();
        workingGroupDetails.addOpenedChangeListener(e -> {
            if (e.isOpened() && needsReload.contains("workingGroups")) {
                workingGroupsGrid.refresh();
                needsReload.remove("workingGroups");
            }
        });

        add(assignmentsDetails, workingGroupDetails);

        addAttachListener(e -> {
                committeeAssignmentsGrid.init();
                workingGroupsGrid.init();
                refreshTermCombo();
        });

        committeeTermsComboBox.addValueChangeListener(e -> {
            committeeAssignmentsGrid.setTermId(e.getValue().id());
            workingGroupsGrid.setTermId(e.getValue().id());
            refreshOpenGrids();
        });
    }

    public void setCommitteeId(Long committeeId) {
        committeeTermsComboBox.setCommitteeId(committeeId);
    }

    public void setSelectedTermId(Long termId) {
        committeeTermsComboBox.setSelectedTermId(termId);
    }

    public void refreshOpenGrids() {
        if (assignmentsDetails.isOpened()) {
            committeeAssignmentsGrid.refresh();
        } else {
            needsReload.add("assignments");
        }

        if (workingGroupDetails.isOpened()) {
            workingGroupsGrid.refresh();
        } else {
            needsReload.add("workingGroups");
        }
    }

    public void refreshTermCombo() {
        committeeTermsComboBox.refresh();
    }

    public void refresh() {
        refreshTermCombo();
        refreshOpenGrids();
    }

    public void init() {
        committeeAssignmentsGrid.init();
        workingGroupsGrid.init();
    }
}
