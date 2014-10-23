package org.spearal.examples.android.conf;

import android.os.AsyncTask;
import android.util.Log;

import org.spearal.DefaultSpearalFactory;
import org.spearal.SpearalFactory;
import org.spearal.examples.android.data.Person;
import org.spearal.examples.android.pagination.PaginatedListWrapper;
import org.spearal.filter.SpearalPropertyFilterBuilder;
import org.spearal.impl.alias.PackageTranslatorAliasStrategy;
import org.spearal.spring.rest.SpearalEntity;
import org.spearal.spring.rest.SpearalMessageConverter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public abstract class AbstractRestAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final RestTemplate restTemplate;
    protected boolean success = false;

    protected AbstractRestAsyncTask() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new SpearalMessageConverter(SpearalFactoryHolder.getInstance()));
    }

    @Override
    protected Result doInBackground(Params... params) {
        try {
            Result result = doRestCall(params);
            success = true;
            return result;
        }
        catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Result result) {
        if (success)
            onRestSuccess(result);
    }

    private static String url(String path) {
        return "http://192.168.0.12:8080/spring-angular/resources" + path;
    }

    protected final <R> R getForObject(String path, Class<R> resultClass, Object... params) {
        return restTemplate.getForObject(url(path), resultClass, params);
    }

    protected final <R> R getFiltered(String path, Class<R> resultClass, SpearalPropertyFilterBuilder filter, Object... params) {
        SpearalEntity<Params> filterEntity = new SpearalEntity<Params>(SpearalFactoryHolder.getInstance(), null, null, filter);
        ResponseEntity<R> responseEntity = restTemplate.exchange(url(path), HttpMethod.GET, filterEntity, resultClass, params);
        return responseEntity.getBody();
    }

    protected final Result postForObject(String path, Class<Result> resultClass, Params object) {
        return restTemplate.postForObject(url(path), object, resultClass);
    }

    protected final void delete(String path, Object... params) {
        restTemplate.delete(url(path), params);
    }

    protected abstract Result doRestCall(Params... params);

    protected abstract void onRestSuccess(Result result);
}
