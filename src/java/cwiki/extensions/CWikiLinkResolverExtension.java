package cwiki.extensions;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.options.MutableDataHolder;

public class CWikiLinkResolverExtension implements HtmlRenderer.HtmlRendererExtension {
    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    @Override
    public void extend(final HtmlRenderer.Builder rendererBuilder, final String rendererType) {
        rendererBuilder.linkResolverFactory(new CWikiLinkResolver.Factory());
    }

    public static CWikiLinkResolverExtension create() {
        return new CWikiLinkResolverExtension();
    }}
