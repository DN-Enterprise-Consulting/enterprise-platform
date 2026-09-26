package de.dn.enterprise.platform.search.retrieval;

import de.dn.enterprise.platform.search.query.SearchQuery;
import de.dn.enterprise.platform.search.query.SearchResult;

import java.util.List;

/** Application boundary for executing search queries. */
public interface SearchRetrieval {
    List<SearchResult> search(SearchQuery query);
}
