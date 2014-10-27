package org.spearal.examples.android;

import org.spearal.examples.android.conf.AbstractRestAsyncTask;
import org.spearal.examples.android.data.Person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

/**
 * An activity representing a list of People. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link PersonDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PersonListFragment} and the item details (if present) is a
 * {@link PersonDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PersonListFragment.Callbacks} interface to listen for item selections.
 */
public class PersonListActivity extends FragmentActivity implements PersonListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

    private static final String URL1 = "https://examples-spearal.rhcloud.com/spring-angular/resources";
    private static final String URL2 = "https://examples-spearal.rhcloud.com/spring-angular-v2/resources";

    public static String baseUrl = URL1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.activity_person_list);

		if (findViewById(R.id.person_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((PersonListFragment) getSupportFragmentManager().findFragmentById(
					R.id.person_list)).setActivateOnItemClick(true);
		}
		
		// TODO: If exposing deep links into your app, handle intents here.
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_new:
                onItemSelected(null);
                return true;
            case R.id.menu_url1:
                changeUrl(URL1);
                return true;
            case R.id.menu_url2:
                changeUrl(URL2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeUrl(String url) {
        baseUrl = url;
        ((ArrayAdapter)((ListFragment)getSupportFragmentManager().findFragmentById(R.id.person_list)).getListAdapter()).notifyDataSetChanged();
        if (mTwoPane)
            onItemSelected(null);
    }

	/**
	 * Callback method from {@link PersonListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Person person) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
            if (person != null)
			    arguments.putLong(PersonDetailFragment.ARG_ITEM_ID, person.getId());
			PersonDetailFragment fragment = new PersonDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.person_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, PersonDetailActivity.class);
            if (person != null)
			    detailIntent.putExtra(PersonDetailFragment.ARG_ITEM_ID, person.getId());
			startActivity(detailIntent);
		}
	}

    @Override
    public boolean isTwoPane() {
        return mTwoPane;
    }
}
