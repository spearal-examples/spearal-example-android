package org.spearal.examples.android;

import org.spearal.Spearal;
import org.spearal.configuration.Introspector;
import org.spearal.examples.android.conf.AbstractRestAsyncTask;
import org.spearal.examples.android.conf.LoadImageTask;
import org.spearal.examples.android.conf.SpearalFactoryHolder;
import org.spearal.examples.android.data.Person;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * A fragment representing a single Person detail screen. This fragment is
 * either contained in a {@link PersonListActivity} in two-pane mode (on
 * tablets) or a {@link PersonDetailActivity} on handsets.
 */
public class PersonDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PersonDetailFragment() {
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
            new LoadPersonTask().execute(getArguments().getLong(ARG_ITEM_ID));
        else {
            // setPerson(new Person());
            setPerson(SpearalFactoryHolder.create(Person.class));
        }
    }

    private Person person;

    private void setPerson(Person person) {
        this.person = person;
        if (getView() == null)
            return;

        ((TextView)getView().findViewById(R.id.form_name)).setText(person.getName());
        ((TextView)getView().findViewById(R.id.form_description)).setText(person.getDescription());
        ((TextView)getView().findViewById(R.id.form_imageurl)).setText(person.getImageUrl());
        new LoadImageTask(((ImageView)getView().findViewById(R.id.form_image))).execute(person.getImageUrl());
    }

    private void showList() {
        if (PersonDetailFragment.this.getActivity().getClass() != PersonListActivity.class)
            NavUtils.navigateUpTo(PersonDetailFragment.this.getActivity(), new Intent(PersonDetailFragment.this.getActivity(), PersonListActivity.class));
        else
            ((ArrayAdapter)((ListFragment)((FragmentActivity)PersonDetailFragment.this.getActivity()).getSupportFragmentManager().findFragmentById(R.id.person_list)).getListAdapter()).notifyDataSetChanged();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_person_detail, container, false);
		
		rootView.findViewById(R.id.button_save).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View button) {
                applyTextValue(person, "name", getView(), R.id.form_name);
                applyTextValue(person, "description", getView(), R.id.form_description);
                applyTextValue(person, "imageUrl", getView(), R.id.form_imageurl);
                new SavePersonTask().execute(person);
            }
        });
		
		rootView.findViewById(R.id.button_delete).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View button) {
                new DeletePersonTask().execute(person);
            }
        });
		
		return rootView;
	}

    private static void applyTextValue(Object object, String propertyName, View rootView, int textViewId) {
        try {
            Method getter = object.getClass().getMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            Object currentValue = getter.invoke(object);
            String value = ((TextView)rootView.findViewById(textViewId)).getText().toString();
            if (value.equals(currentValue))
                Spearal.undefine(object, propertyName);
            else {
                Method setter = object.getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), String.class);
                setter.invoke(object, value);
            }
        }
        catch (Exception e) {
            // Should probably do something
        }
    }

	private class LoadPersonTask extends AbstractRestAsyncTask<Long, Void, Person> {
		
        @Override
        protected Person doRestCall(Long... params) {
            return getForObject("/persons/{id}", Person.class, params[0]);
        }
        
        @Override
        protected void onRestSuccess(final Person person) {
            setPerson(person);
        }
    }
	
	private class SavePersonTask extends AbstractRestAsyncTask<Person, Void, Person> {
		
        @Override
        protected Person doRestCall(Person... params) {
            return postForObject("/persons", Person.class, params[0]);
        }
        
        @Override
        protected void onRestSuccess(final Person person) {
            showList();
        }
    }
	
	private class DeletePersonTask extends AbstractRestAsyncTask<Person, Void, Void> {
		
        @Override
        protected Void doRestCall(Person... params) {
            delete("/persons/{id}", params[0].getId());
            return null;
        }
        
        @Override
        protected void onRestSuccess(final Void result) {
            showList();
        }
    }
}
