package feign.template;

import java.nio.charset.Charset;

/**
 * @author RMakhmutov
 * @since 29.07.2020
 */
public class JaxrsUriTemplate extends Template {
    public static JaxrsUriTemplate create(String template, boolean encodeSlash, Charset charset) {
        return new JaxrsUriTemplate(template, encodeSlash, charset);
    }

    private JaxrsUriTemplate(String template, boolean encodeSlash, Charset charset) {
        super(template, ExpansionOptions.ALLOW_UNRESOLVED, Template.EncodingOptions.REQUIRED, encodeSlash, charset);
    }
}
