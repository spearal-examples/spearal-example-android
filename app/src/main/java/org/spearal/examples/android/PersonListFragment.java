package org.spearal.examples.android;

import java.util.List;

import org.spearal.examples.android.conf.AbstractRestAsyncTask;
import org.spearal.examples.android.data.Person;
import org.spearal.examples.android.pagination.PaginatedListWrapper;
import org.spearal.filter.SpearalPropertyFilterBuilder;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A list fragment representing a list of People. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link PersonDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PersonListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Person person);

        public boolean isTwoPane();
    }

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {

		@Override
		public void onItemSelected(Person person) {
		}

        @Override
        public boolean isTwoPane() {
            return false;
        }
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PersonListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();

		new LoadPersonsTask().execute();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		
		mCallbacks = (Callbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

        mActivatedPosition = position;

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected((Person)listView.getAdapter().getItem(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	
	private class LoadPersonsTask extends AbstractRestAsyncTask<Void, Void, List<Person>> {
		
        @SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
        protected List<Person> doRestCall(Void... params) {
            SpearalPropertyFilterBuilder filter = mCallbacks.isTwoPane()
                    ? SpearalPropertyFilterBuilder.of(Person.class, "name", "description")
                    : SpearalPropertyFilterBuilder.of(Person.class, "name");

            return getFiltered("/persons?pageSize=100", PaginatedListWrapper.class, filter).getList();
        }
        
        @Override
        protected void onRestSuccess(final List<Person> persons) {
            if (mCallbacks.isTwoPane()) {
                setListAdapter(new ArrayAdapter<Person>(getActivity(),
                        R.layout.list_item_large, android.R.id.text1, persons
                ) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ((TextView) view.findViewById(android.R.id.text1)).setText(persons.get(position).getName());
                        ((TextView) view.findViewById(android.R.id.text2)).setText(persons.get(position).getDescription());
                        return view;
                    }
                });

                setActivatedPosition(mActivatedPosition);
            }
            else {
                setListAdapter(new ArrayAdapter<Person>(getActivity(),
                        android.R.layout.simple_list_item_activated_1, android.R.id.text1, persons
                ) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ((TextView) view.findViewById(android.R.id.text1)).setText(persons.get(position).getName());
                        return view;
                    }
                });
            }

            getListAdapter().registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    new LoadPersonsTask().execute();
                }
            });
        }
    }
}
