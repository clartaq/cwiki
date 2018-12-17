package cwiki.extensions;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.ResolvedLink;

import java.util.Set;

public class CWikiLinkResolver implements LinkResolver {

    private CWikiLinkResolver(final LinkResolverContext context) {
        // can use context for custom settings
        // context.getDocument();
        // context.getHtmlOptions();
    }

    // From: https://stackoverflow.com/questions/724043/http-url-address-encoding-in-java

    /**
     * Percent-encodes a string so it's suitable for use in a URL Path (not a
     * query string / form encode, which uses + for spaces, etc)
     */
    private static String percentEncode(String encodeMe) {
        if (encodeMe == null) {
            return "";
        }
        String encoded = encodeMe.replace("%", "%25");
        encoded = encoded.replace(" ", "%20");
        encoded = encoded.replace("!", "%21");
        encoded = encoded.replace("#", "%23");
        encoded = encoded.replace("$", "%24");
        encoded = encoded.replace("&", "%26");
        encoded = encoded.replace("'", "%27");
        encoded = encoded.replace("(", "%28");
        encoded = encoded.replace(")", "%29");
        encoded = encoded.replace("*", "%2A");
        encoded = encoded.replace("+", "%2B");
        encoded = encoded.replace(",", "%2C");
        encoded = encoded.replace("/", "%2F");
        encoded = encoded.replace(":", "%3A");
        encoded = encoded.replace(";", "%3B");
        encoded = encoded.replace("=", "%3D");
        encoded = encoded.replace("?", "%3F");
        encoded = encoded.replace("@", "%40");
        encoded = encoded.replace("[", "%5B");
        encoded = encoded.replace("]", "%5D");
        return encoded;
    }

    private final static String AS_TAG_PREFIX = "/as-tag?tag=";
    private final static String AS_USER_PREFIX = "/as-user?user=";

    @Override
    public ResolvedLink resolveLink(final Node node, final LinkResolverContext context, final ResolvedLink link) {
        // you can also set/clear/modify attributes through ResolvedLink.getAttributes() and ResolvedLink.getNonNullAttributes()
        if (node instanceof WikiLink) {
            String url = link.getUrl();
            String newUrl;
            if (url.startsWith(AS_TAG_PREFIX)) {
                String theTag = url.replace(AS_TAG_PREFIX, "");
                newUrl = AS_TAG_PREFIX + percentEncode(theTag);
            } else if (url.startsWith(AS_USER_PREFIX)) {
                String theUser = url.replace(AS_USER_PREFIX, "");
                newUrl = AS_USER_PREFIX + percentEncode(theUser);
            } else {
                newUrl = percentEncode(url);
            }

            // resolve url, return one of LinkStatus other than LinkStatus.UNKNOWN
            return link.withStatus(LinkStatus.VALID)
                    .withUrl(newUrl);
        }
        return link;
    }

    static class Factory implements LinkResolverFactory {
        @Override
        public Set<Class<? extends LinkResolverFactory>> getAfterDependents() {
            return null;
        }

        @Override
        public Set<Class<? extends LinkResolverFactory>> getBeforeDependents() {
            return null;
        }

        @Override
        public boolean affectsGlobalScope() {
            return false;
        }

        @Override
        public LinkResolver create(final LinkResolverContext context) {
            return new CWikiLinkResolver(context);
        }
    }
}
