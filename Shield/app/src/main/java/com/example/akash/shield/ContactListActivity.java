package com.example.akash.shield;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import com.example.akash.adapters.ContactsListAdapter;
import com.example.akash.fragment.AllContactFragment;
import com.example.akash.fragment.FavouriteContactFragment;

// Tab Activity class that shows up user's all contacts from the device contacts inventory in one tab and the other tab contains only
// the contacts added as favorites by the user itself; User can also search to select one from the specified contacts' lists

public class ContactListActivity extends FragmentActivity implements
		ActionBar.TabListener {

	// Declaring global variables
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "All Contacts", "Favourites" };

	private SearchView searchView;

	// Initializing with static instances of the the Adapter
	private ContactsListAdapter mContactsListAdapter = AllContactFragment.mContactsListAdapter;
	private ContactsListAdapter mContactsListAdapterFavorites = FavouriteContactFragment.mContactsListAdapter;

	private String regexStrNum = "^[0-9]*$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contact_list_layout);


		// Instantiating and initializing variables declared earlier
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar=getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		// Setting action bar attributes
//		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setIcon(R.mipmap.ic_launcher);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		// View Pager on page change event listener;
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			// On specific page selected by position callback of OnPageChangeListener
			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// makes respective tab by position selected
				actionBar.setSelectedNavigationItem(position);
				// Setting the search view query to default along with removing the focus for the same
				searchView.setQuery("", false);
				searchView.clearFocus();

				// Setting default filter to the respective tab list's Adapter along with setting default empty list texts
				if(position == 0)
				{
					AllContactFragment.tvDummyText.setText("No contact's available!");
					mContactsListAdapter = AllContactFragment.mContactsListAdapter;
					mContactsListAdapter.getFilter().filter("");
				}

				if (position == 1)
				{
					FavouriteContactFragment.tvDummyText.setText("No contact's available!");
					mContactsListAdapterFavorites = FavouriteContactFragment.mContactsListAdapter;
					mContactsListAdapterFavorites.getFilter().filter("");
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	// On Options Menu Creation callback for this activity; initializes and sets listener to the Search view at action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_search, menu);

		// Initializing the searchview
		searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

		// Initializing the OnQueryTextListener and setting to the searchview
		SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
		{

			// On query text change callback; filters the respective tab's list adapter with the text typed at the search box
			@Override
			public boolean onQueryTextChange(String newText)
			{
				// On all contacts' list fragment selected; checks the entered query text and sets empty list view texts accordingly
				if(viewPager.getCurrentItem() == 0) {

					// As the query text is a 10 digit number(valid mobile number), setting the empty list view text highlighted to be availed as a selected
					// number to send money to when clicked on the same
					if (newText.matches(regexStrNum) && newText.length() == 10)
					{
						String text = "<font color=#000000>Send to</font> <font color=#0404B4>"+newText+"</font> <font color=#000000>?</font>";
						AllContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is a number, setting the empty list view text highlighted
					else if (newText.matches(regexStrNum) && newText.length() < 10)
					{
						String text = "<font color=#0404B4>"+newText+"</font>";
						AllContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is not a number, just setting default empty list view text
					else
					{
						AllContactFragment.tvDummyText.setText("No contact's available.");
					}
					// Finally Initializing the adapter with static instance and setting filter text to the same
					mContactsListAdapter = AllContactFragment.mContactsListAdapter;
					mContactsListAdapter.getFilter().filter(newText);

				}
				// On favorite contacts' list fragment selected; checks the entered query text and sets empty list view texts accordingly
				if(viewPager.getCurrentItem() == 1) {

					// As the query text is a 10 digit number(valid mobile number), setting the empty list view text highlighted to be availed as a selected
					// number to send money to when clicked on the same
					if (newText.matches(regexStrNum) && newText.length() == 10)
					{
						String text = "<font color=#000000>Send to</font> <font color=#0404B4>"+newText+"</font> <font color=#000000>?</font>";
						FavouriteContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is a number, setting the empty list view text highlighted
					else if (newText.matches(regexStrNum) && newText.length() < 10)
					{
						String text = "<font color=#0404B4>"+newText+"</font>";
						FavouriteContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is not a number, just setting default empty list view text
					else
					{
						FavouriteContactFragment.tvDummyText.setText("No contact's available.");
					}
					// Finally Initializing the adapter with static instance and setting filter text to the same
					mContactsListAdapterFavorites = FavouriteContactFragment.mContactsListAdapter;
					mContactsListAdapterFavorites.getFilter().filter(newText);
				}
				return true;
			}

			// On query text submit callback; filters the respective tab's list adapter with the text typed at the search box when submitted
			@Override
			public boolean onQueryTextSubmit(String query)
			{

				// On all contacts' list fragment selected; checks the entered query text and sets empty list view texts accordingly
				if(viewPager.getCurrentItem() == 0) {

					// As the query text is a 10 digit number(valid mobile number), setting the empty list view text highlighted to be availed as a selected
					// number to send money to when clicked on the same
					if (query.matches(regexStrNum) && query.length() == 10)
					{
						String text = "<font color=#000000>Send to</font> <font color=#0404B4>"+query+"</font> <font color=#000000>?</font>";
						AllContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is a number, setting the empty list view text highlighted
					else if (query.matches(regexStrNum) && query.length() < 10)
					{
						String text = "<font color=#0404B4>"+query+"</font>";
						AllContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is not a number, just setting default empty list view text
					else
					{
						AllContactFragment.tvDummyText.setText("No contact's available.");
					}
					// Finally Initializing the adapter with static instance and setting filter text to the same
					mContactsListAdapter = AllContactFragment.mContactsListAdapter;
					mContactsListAdapter.getFilter().filter(query);

				}
				// On favorite contacts' list fragment selected; checks the entered query text and sets empty list view texts accordingly
				if(viewPager.getCurrentItem() == 1) {

					// As the query text is a 10 digit number(valid mobile number), setting the empty list view text highlighted to be availed as a selected
					// number to send money to when clicked on the same
					if (query.matches(regexStrNum) && query.length() == 10)
					{
						String text = "<font color=#000000>Send to</font> <font color=#0404B4>"+query+"</font> <font color=#000000>?</font>";
						FavouriteContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is a number, setting the empty list view text highlighted
					else if (query.matches(regexStrNum) && query.length() < 10)
					{
						String text = "<font color=#0404B4>"+query+"</font>";
						FavouriteContactFragment.tvDummyText.setText(Html.fromHtml(text));
					}
					// As the query text is not a number, just setting default empty list view text
					else
					{
						FavouriteContactFragment.tvDummyText.setText("No contact's available.");
					}
					// Finally Initializing the adapter with static instance and setting filter text to the same
					mContactsListAdapterFavorites = FavouriteContactFragment.mContactsListAdapter;
					mContactsListAdapterFavorites.getFilter().filter(query);
				}
				return true;
			}
		};
		searchView.setOnQueryTextListener(textChangeListener);

		return super.onCreateOptionsMenu(menu);

	}


	// Tab listener tab selected callbacks
	// On onTabReselected callback for the tab listener
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
	// On onTabSelected callback for the tab listener; setting view pager current item with selected tab's position
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// showing respected fragment view at view pager
		viewPager.setCurrentItem(tab.getPosition());
	}
	// On onTabUnselected callback for the tab listener
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	// FragmentPagerAdapter class that sets respective fragment to the viewpager
	class TabsPagerAdapter extends FragmentPagerAdapter {

		public TabsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			switch (index) {
				case 0:
					// All Contacts' list fragment
					return new AllContactFragment();
				case 1:
					// Favorite contacts' list fragment
					return new FavouriteContactFragment();
			}

			return null;
		}

		@Override
		public int getCount() {
			// get item count - equal to number of tabs
			return 2;
		}
	}

}

