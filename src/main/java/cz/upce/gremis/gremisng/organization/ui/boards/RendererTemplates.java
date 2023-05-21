package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import cz.upce.gremis.gremisng.organization.domain.BoardListItemView;
import lombok.experimental.UtilityClass;

@UtilityClass
class RendererTemplates {

    public static final String NAME_AND_ABBREVIATION_TEMPLATE = "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
            + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
            + "    <span> ${item.name} </span>"
            + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
            + "      ${item.abbreviation}" + "    </span>"
            + "  </vaadin-vertical-layout>"
            + "</vaadin-horizontal-layout>";

    public static <T> Renderer<T> createNameAndAbbreviationRenderer(ValueProvider<T, ?> nameProvider,
                                                                    ValueProvider<T, ?> abbreviationProvider) {
        return LitRenderer.<T>of(NAME_AND_ABBREVIATION_TEMPLATE)
                .withProperty("name", nameProvider)
                .withProperty("abbreviation", abbreviationProvider);
    }


}
