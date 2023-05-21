package cz.upce.gremis.gremisng.ui;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Log4j2
@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "translate";


    public final Locale LOCALE_EN = new Locale("en", "GB");

    private List<Locale> locales = List.of(LOCALE_EN);

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            log.warn("Got lang request for key with null value!");
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            log.warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }
}