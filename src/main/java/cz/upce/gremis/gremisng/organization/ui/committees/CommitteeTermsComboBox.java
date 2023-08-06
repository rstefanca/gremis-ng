package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.dataview.ComboBoxLazyDataView;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.CommitteeTermRepository;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Optional;
import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommitteeTermsComboBox extends ComboBox<TermDto> {

    private static final String TERM_COMBO_TEMPLATE =
            """
                    <div style="display: flex;">
                        <div>
                            ${item.description}
                            <div style=\\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\\">${item.from} - ${item.to}</div>
                        </div>
                    </div>""";

    private Long committeeId;

    private final CommitteeTermRepository committeeTermRepository;

    private final ComboBoxLazyDataView<TermDto> dataView;


    public CommitteeTermsComboBox(CommitteeTermRepository committeeTermRepository) {
        this.committeeTermRepository = committeeTermRepository;
        setAllowCustomValue(false);
        dataView = setItems(new DataProvider());
        setItemLabelGenerator(termDto -> String.format("%s (%s - %s)",termDto.description(), termDto.start(), termDto.end()));
        setLabel("Funkční období");
        setRenderer(createTermRenderer());
    }

    public void setCommitteeId(Long committeeId) {
        this.committeeId = committeeId;
    }

    public void setSelectedTermId(Long termId) {
        if (termId != null) {
            dataView.getItems()
                    .filter(i -> i.id().equals(termId))
                    .findFirst()
                    .ifPresent(this::setValue);
        }
    }

    private class DataProvider implements CallbackDataProvider.FetchCallback<TermDto, String> {

        @Override
        public Stream<TermDto> fetch(Query<TermDto, String> query) {
            var springPageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query); //cheat
            return committeeTermRepository.getTerms(committeeId).stream();
        }
    }

    public Optional<TermDto> getItemById(Long id) {
        return dataView.getItems().filter(it -> it.id().equals(id)).findFirst();
    }

    public void refresh() {
        dataView.refreshAll();
        if (getValue() == null) {
            dataView.getItems()
                    .findFirst()
                    .ifPresent(this::setValue);
        }
    }

    private Renderer<TermDto> createTermRenderer() {
        return LitRenderer.<TermDto>of(TERM_COMBO_TEMPLATE)
                .withProperty("description", TermDto::description)
                .withProperty("from", term -> term.start().toString())
                .withProperty("to", term -> term.end().toString());
    }
}
