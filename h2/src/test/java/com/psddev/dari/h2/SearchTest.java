package com.psddev.dari.h2;

import com.psddev.dari.db.Query;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SearchTest extends AbstractTest {

    private static final String FOO = "foo";

    @After
    public void deleteModels() {
        Query.from(SearchModel.class).deleteAll();
    }

    @Test
    public void searchOne() {
        Stream.of(FOO, "bar", "qux").forEach(string -> {
            SearchModel model = new SearchModel();
            model.one = string;
            model.set.add(FOO);
            model.list.add(FOO);
            model.map.put(FOO, FOO);
            model.save();
        });

        List<SearchModel> fooResult = Query
                .from(SearchModel.class)
                .where("one matches ?", FOO)
                .selectAll();

        assertThat(fooResult, hasSize(1));
        assertThat(fooResult.get(0).one, equalTo(FOO));
    }

    @Test
    public void searchSet() {
        Stream.of(FOO, "bar", "qux").forEach(string -> {
            SearchModel model = new SearchModel();
            model.one = FOO;
            model.set.add(string);
            model.list.add(FOO);
            model.map.put(FOO, FOO);
            model.save();
        });

        List<SearchModel> fooResult = Query
                .from(SearchModel.class)
                .where("set matches ?", FOO)
                .selectAll();

        assertThat(fooResult, hasSize(1));
        assertThat(fooResult.get(0).set, hasSize(1));
        assertThat(fooResult.get(0).set.iterator().next(), equalTo(FOO));
    }

    @Test
    public void searchList() {
        Stream.of(FOO, "bar", "qux").forEach(string -> {
            SearchModel model = new SearchModel();
            model.one = FOO;
            model.set.add(FOO);
            model.list.add(string);
            model.map.put(FOO, FOO);
            model.save();
        });

        List<SearchModel> fooResult = Query
                .from(SearchModel.class)
                .where("list matches ?", FOO)
                .selectAll();

        assertThat(fooResult, hasSize(1));
        assertThat(fooResult.get(0).list, hasSize(1));
        assertThat(fooResult.get(0).list.get(0), equalTo(FOO));
    }

    @Test
    public void searchMap() {
        Stream.of(FOO, "bar", "qux").forEach(string -> {
            SearchModel model = new SearchModel();
            model.one = FOO;
            model.set.add(FOO);
            model.list.add(FOO);
            model.map.put(string, string);
            model.save();
        });

        List<SearchModel> fooResult = Query
                .from(SearchModel.class)
                .where("map matches ?", FOO)
                .selectAll();

        assertThat(fooResult, hasSize(1));
        assertThat(fooResult.get(0).map.size(), equalTo(1));
        assertThat(fooResult.get(0).map.values().iterator().next(), equalTo(FOO));
    }

    @Test
    public void searchAny() {
        Stream.of(FOO, "bar", "qux").forEach(string -> {
            SearchModel model = new SearchModel();
            model.one = string;
            model.set.add(FOO);
            model.save();
        });

        List<SearchModel> fooResult = Query
                .from(SearchModel.class)
                .where("_any matches ?", FOO)
                .selectAll();

        assertThat(fooResult, hasSize(3));
    }
}
