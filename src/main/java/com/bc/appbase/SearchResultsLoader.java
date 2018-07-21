/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.appbase;

import com.bc.appbase.ui.MainFrame;
import com.bc.appbase.ui.SearchResultsPanel;
import com.bc.appcore.jpa.SearchContext;
import static com.bc.appcore.jpa.model.EntityRelation.logger;
import com.bc.jpa.predicates.DatabaseCommunicationsFailureTest;
import com.bc.jpa.search.SearchResults;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * @author Chinomso Bassey Ikwuagwu on May 17, 2018 12:11:04 PM
 */
public class SearchResultsLoader<T> extends SwingWorker<SearchResults<T>, SearchResults<T>> {

    private static final Logger LOG = Logger.getLogger(SearchResultsLoader.class.getName());
    
    public static final class ExceptionHandler implements Consumer<Exception> {
        private final Predicate<Throwable> reThowExceptionTest;
        public ExceptionHandler(Predicate<Throwable> reThowExceptionTest) {
            this.reThowExceptionTest = Objects.requireNonNull(reThowExceptionTest);
        }
        @Override
        public void accept(Exception e) {
            if(this.reThowExceptionTest.test(e)) {
                if(e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }else{
                    throw new RuntimeException(e);    
                }
            }else{
                logger.warning(e.toString());
            }
        }
    }
    
    private final SearchContext<T> searchContext;
    
    private final Consumer<Exception> exceptionHandler;
    
    private final SearchResultsPanel resultsPanel;
    
    private final String id;

    public SearchResultsLoader(App app, Class<T> entityType) {
        this(app.getSearchContext(entityType), 
                new ExceptionHandler(app.getOrException(DatabaseCommunicationsFailureTest.class).negate()), 
                ((MainFrame)app.getUIContext().getMainFrame()).getSearchResultsPanel(), 
                "AppMainFrame");
    }

    public SearchResultsLoader(
            SearchContext<T> searchContext, 
            Consumer<Exception> exceptionHandler, 
            SearchResultsPanel resultsPanel, 
            String id) {
        this.searchContext = Objects.requireNonNull(searchContext);
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
        this.resultsPanel = Objects.requireNonNull(resultsPanel);
        this.id = Objects.requireNonNull(id);
    }

    @Override
    protected SearchResults<T> doInBackground() throws Exception {
        
        SearchResults<T> searchResults = SearchResults.EMPTY_INSTANCE;
        
        try{
            searchResults = searchContext.searchAll();
        }catch(RuntimeException e) {
            exceptionHandler.accept(e);
        }
        
        return searchResults;
    }

    @Override
    protected void done() {
        try{

            final SearchResults searchResults = this.get();

            resultsPanel.load(searchContext, searchResults, id, 0, 1, true);

        }catch(InterruptedException | ExecutionException e) {

            LOG.log(Level.WARNING, "Unexpected exception loading search results on to " + this.resultsPanel.getClass().getName(), e);
        }
    }
}
