package me.declangao.wordpressreader.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;


import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;

import me.declangao.wordpressreader.R;
import me.declangao.wordpressreader.adaptor.MyRecyclerViewCategoriesAdaptor;
import me.declangao.wordpressreader.model.Category;
import me.declangao.wordpressreader.util.Config;
import me.declangao.wordpressreader.util.JSONParser;

/**
 * Fragment to display TabLayout and ViewPager.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.CategoryListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment implements SearchView.OnQueryTextListener, RadioListener {
    private static final String TAG = "MainFragment";

    DrawerLayout drawer;
    ActionBarDrawerToggle drawerToggle;

    private ProgressDialog mProgressDialog;
    //private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    NavigationView navigationView;
    private Menu menu;
    private Menu submenu;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewCategoriesAdaptor mAdaptor;
    private LinearLayoutManager mLayoutManager;
    // Widget to show user a loading message
    private TextView mLoadingView;

    // A flag to keep track if the app is currently loading new posts
    private boolean isLoading = false;

    private int mPage = 1; // Page number
    private int mCatId; // Category ID
    private int mPreviousPostNum = 0; // Number of posts in the list
    private int mPostNum; // Number of posts in the "new" list
    private String mQuery = ""; // Query string used for search result
    // Flag to determine if current fragment is used to show search result
    private boolean isSearch = false;

    // Keep track of the list items
    private int mPastVisibleItems;
    private int mVisibleItemCount;

    RadioManager mRadioManager;
    ImageButton mButtonControlStart;
    TextView mTextViewMetaData;

    // List of all categories
    protected static ArrayList<Category> categories = null;

    private CategoryListener mListener;
    //private PlayButtonListener pListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Stops onDestroy() and onCreate() being called when the parent
        // activity is destroyed/recreated on configuration change
        setRetainInstance(true);

        // Display a search menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_layout, container, false);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appBarLayout);
        //appBarLayout.setElevation(0);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        //toolbar.setElevation(0);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        //((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                (MainActivity) getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((MainActivity) getActivity());

         // Create menu on the fly
        /*
        menu = navigationView.getMenu();
        submenu = menu.addSubMenu("New Super SubMenu");
        submenu.add("Super Item1");
        submenu.add("Super Item2");
        submenu.add("Super Item3");

        navigationView.invalidate();
         */

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mLoadingView = (TextView) rootView.findViewById(R.id.text_view_loading);
        mLayoutManager = new LinearLayoutManager(getActivity());


        mRecyclerView.setHasFixedSize(true); // Every row in the list has the same size
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdaptor);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            // Automatically load new posts if end of the list is reached
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLayoutManager.getChildCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();

                if (mPostNum > mPreviousPostNum && !categories.isEmpty() && mVisibleItemCount != 0 &&
                        totalItemCount > mVisibleItemCount && !isLoading &&
                        (mVisibleItemCount + mPastVisibleItems) >= totalItemCount) {
                    //loadNextPage();
                    // Update post number
                    mPreviousPostNum = mPostNum;
                }
            }
        });


        //mRadioManager = RadioManager.with(((MainActivity) getActivity()).getApplicationContext());
        mRadioManager = AppController.getInstance().getRadioManager();
        mRadioManager.registerListener(this);
        mRadioManager.setLogging(true);

        mButtonControlStart = (ImageButton) rootView.findViewById(R.id.buttonControlStart);
        mTextViewMetaData = (TextView) rootView.findViewById(R.id.textviewMetaData);

        mButtonControlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRadioManager.isPlaying())
                    mRadioManager.startRadio(Config.RADIO_URL[0]);
                else
                    mRadioManager.stopRadio();
            }
        });

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadCategories();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");

        inflater.inflate(R.menu.menu_main, menu);

        // Create expandable & collapsible SearchView
        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Expanded by default
        //searchView.requestFocus();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);

        /**/

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            searchView.requestFocus();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRadioManager.connect();
    }


    /**
     * Reset the ActionBar to show proper menu and collapse SearchView
     */
    protected void resetActionBar() {
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        searchMenuItem.collapseActionView();
    }

    /**
     * Download categories and create tabs
     */
    private void loadCategories() {
        // Display a progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading_categories));
        // User cannot dismiss it by touching outside the dialog
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        // Make a request to get categories
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Config.CATEGORY_URL,
                null,
                new Response.Listener<JSONObject>() {
                    // Request succeeded
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        mProgressDialog.dismiss();

                        // Get categories from JSON data
                        categories = JSONParser.parseCategories(jsonObject);

                        mAdaptor = new MyRecyclerViewCategoriesAdaptor(categories, new MyRecyclerViewCategoriesAdaptor.OnItemClickListener() {
                            @Override
                            public void onItemClick(Category cat) {
                                mListener.onCategorySelected(cat);
                            }
                        });
                        mRecyclerView.setAdapter(mAdaptor);

                    }
                },
                // Request failed

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "----- Volley Error -----");
                        mProgressDialog.dismiss();
                        // Show an INDEFINITE Snackbar. New in design support lib v22.2.1.

                        Snackbar.make(mRecyclerView, R.string.error_load_categories, Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadCategories();
                                    }
                                }).show();/**/
                    }
                });

        // Add the request to request queue
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus(); // Hide soft keyboard
        mListener.onSearchSubmitted(query); // Deal with fragment transaction on MainActivity
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (CategoryListener) activity;
            //pListener = (PlayButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement PostListListener");
        }
    }


    @Override
    public void onRadioLoading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO Do UI works here.
                //mTextViewControl.setText("RADIO STATE : LOADING...");
            }
        });
    }

    @Override
    public void onRadioConnected() {

    }

    @Override
    public void onRadioStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            //TODO Do UI works here.
            //mTextViewControl.setText("RADIO STATE : PLAYING...");
            mButtonControlStart.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);

            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    @Override
    public void onRadioStopped() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            //TODO Do UI works here
            //mTextViewControl.setText("RADIO STATE : STOPPED.");
            mButtonControlStart.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);

            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    @Override
    public void onMetaDataReceived(final String s, final String s1) {
        //TODO Check metadata values. Singer name, song name or whatever you have.
        if (s != null && s1 != null) {
            final String metaText = s.replace("StreamTitle","")
                    .replace("- - -","")
                    .replace(" - "," ")+ " " + s1.replace("- - -","");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO Do UI works here.
                    if(metaText.length() > 52)
                    mTextViewMetaData.setText(metaText.substring(0,52)+"...");
                    else
                    mTextViewMetaData.setText(metaText);
                }
            });
        }
    }


    @Override
    public void onError() {

    }

    // Interface used to communicate with MainActivity
    public interface CategoryListener {
        void onCategorySelected(Category cat);
        void onSearchSubmitted(String query);
    }

}
