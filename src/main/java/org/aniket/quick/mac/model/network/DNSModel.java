package org.aniket.quick.mac.model.network;

import lombok.Getter;
import lombok.Setter;
import org.aniket.quick.mac.model.Attribute;
import org.aniket.quick.mac.model.network.DNSAttribute;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
public class DNSModel {

    private final Attribute nameservers;
    private final Attribute serachDomains;
    private final Attribute timeout;
    private static final Map<String, DNSAttribute> rawTextMapping = new HashMap<>();
    static {
        rawTextMapping.put("nameserver", DNSAttribute.NAMESERVERS);
        rawTextMapping.put("search domain", DNSAttribute.SEARCH_DOMAINS);
        rawTextMapping.put("timeout", DNSAttribute.RESOLVER_TIMEOUT);
    }

    public DNSModel(final Attribute nameservers, final Attribute serachDomains, final Attribute timeout) {
        this.nameservers = nameservers;
        this.serachDomains = serachDomains;
        this.timeout = timeout;
    }

    public static Optional<DNSAttribute> getDNSAttribute(final String rawText) {
        final Optional<Map.Entry<String, DNSAttribute>> attr = rawTextMapping.entrySet().stream()
                .filter(entry -> rawText.contains(entry.getKey()))
                .findFirst();
        return attr.isPresent() ? Optional.of(attr.get().getValue()) : Optional.empty();
    }

    public static String getValue(final DNSAttribute dnsAttribute, final List<String> values) {
        switch (dnsAttribute) {
            case NAMESERVERS, SEARCH_DOMAINS -> {
                return Attribute.getCLITextForList(values);
            }
            case RESOLVER_TIMEOUT ->  {
                return values.stream().map(Integer::parseInt).min(Comparator.naturalOrder()).get().toString();
            }
            case LOCAL_DOMAIN, DNS_LOGGING -> {
                return values.get(0);
            }
        }
        return null;
    }
}
