package de.dn.enterprise.platform.search.retrieval;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.query.SearchQuery;
import de.dn.enterprise.platform.search.query.SearchResult;
import de.dn.enterprise.platform.search.service.SearchService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Deterministic in-process retrieval implementation using the SearchService. */
public final class DefaultSearchRetrieval implements SearchRetrieval {
    private final SearchService service;

    public DefaultSearchRetrieval(SearchService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    @Override
    public List<SearchResult> search(SearchQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        List<SearchResult> results = new ArrayList<>();
        for (SearchDocument document : service.list()) {
            if (!matchesFilters(document, query)) {
                continue;
            }
            int score = score(document, query.text());
            if (query.text() != null && score == 0) {
                continue;
            }
            results.add(new SearchResult(document, score));
        }
        results.sort(Comparator.comparingInt(SearchResult::score).reversed()
                .thenComparing(result -> result.document().id().toString()));
        if (results.size() <= query.limit()) {
            return List.copyOf(results);
        }
        return List.copyOf(results.subList(0, query.limit()));
    }

    private static boolean matchesFilters(SearchDocument document, SearchQuery query) {
        if (query.type() != null && document.type() != query.type()) return false;
        if (query.status() != null && document.status() != query.status()) return false;
        for (Map.Entry<String, String> entry : query.attributes().entrySet()) {
            if (!entry.getValue().equals(document.attributes().get(entry.getKey()))) return false;
        }
        return true;
    }

    private static int score(SearchDocument document, String text) {
        if (text == null) return 0;
        String needle = text.toLowerCase(Locale.ROOT).trim();
        int score = 0;
        score += occurrences(document.metadata().name(), needle) * 5;
        score += occurrences(document.metadata().description(), needle) * 3;
        score += occurrences(document.content(), needle) * 2;
        for (Map.Entry<String, String> entry : document.attributes().entrySet()) {
            score += occurrences(entry.getKey(), needle);
            score += occurrences(entry.getValue(), needle);
        }
        return score;
    }

    private static int occurrences(String value, String needle) {
        String haystack = value.toLowerCase(Locale.ROOT);
        int count = 0;
        int index = 0;
        while ((index = haystack.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
