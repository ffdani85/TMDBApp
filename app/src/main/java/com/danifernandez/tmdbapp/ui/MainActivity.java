package com.danifernandez.tmdbapp.ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.danifernandez.tmdbapp.R;
import com.danifernandez.tmdbapp.imagedownload.DownloadImageAT;
import com.danifernandez.tmdbapp.imagedownload.DownloadImageCompleteListener;
import com.danifernandez.tmdbapp.imagedownload.DownloadImageResult;
import com.danifernandez.tmdbapp.utils.Constants;
import com.danifernandez.tmdbapp.utils.JSONdecoder;
import com.danifernandez.tmdbapp.utils.Movie;
import com.danifernandez.tmdbapp.utils.MovieAdapter;
import com.danifernandez.tmdbapp.utils.MyLog;
import com.danifernandez.tmdbapp.ws.WebService;
import com.danifernandez.tmdbapp.ws.WebServiceAT;
import com.danifernandez.tmdbapp.ws.WebServiceCompleteListener;
import com.danifernandez.tmdbapp.ws.WebServiceResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    private MovieAdapter movieAdapter;
    private ListView listOfMovies;
    private LinearLayout layoutLoading;

    private ArrayList<Movie> popularMovies = new ArrayList<>();
    private int popularCurrentPage = -1;
    private int popularTotalPages = -1;
    private int popularFirstItemVisible = 0;
    private ArrayList<Movie> searchMovies = new ArrayList<>();
    private int searchCurrentPage = -1;
    private int searchTotalPages = -1;

    private boolean needsRefresh = false;
    private boolean showSearch = false;
    private String searchText = null;

    private WebServiceAT lastSearchAT;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MyLog.d(TAG, "search text: " + newText);
                if(newText.isEmpty()){
                    showSearch = false;
                    updateMovies(popularMovies);
                    if(listOfMovies != null){
                        listOfMovies.setSelection(popularFirstItemVisible);
                    }
                }
                else{
                    showSearch = true;
                    searchText = newText;
                    searchMovies(-1);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list);
        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        movieAdapter = new MovieAdapter(this, R.layout.movie_item_row, popularMovies, null);
        listOfMovies = (ListView) findViewById(R.id.movies_list);
        listOfMovies.setAdapter(movieAdapter);
        listOfMovies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemInScreen = firstVisibleItem + visibleItemCount;
                if(!showSearch){
                    popularFirstItemVisible = firstVisibleItem;
                }

                if ((lastItemInScreen == totalItemCount) ) {
                    if(showSearch){
                        if(needsRefresh && searchCurrentPage < searchTotalPages) {
                            searchMovies(searchCurrentPage + 1);
                            needsRefresh = false;
                        }
                    }
                    else{
                        if(needsRefresh && popularCurrentPage < popularTotalPages) {
                            requestPopularMovies(popularCurrentPage + 1);
                            needsRefresh = false;
                        }
                    }

                }}
        });
        requestPopularMovies(-1);
    }

    private void showDialogMessage(String msgTxt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msgTxt);
        builder.setPositiveButton(getResources().getString(R.string.close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateMovies(ArrayList<Movie> movies){
        movieAdapter.updateData(movies);
    }

    private void requestPopularMovies(int page) {
        layoutLoading.setVisibility(View.VISIBLE);
        WebServiceAT wsAT = new WebServiceAT(this,
                new WebServiceOnCompleteListener(),
                Constants.WS_URL_MOVIES_POPULAR,
                WebService.RequestMethod.GET);
        try {
            wsAT.addName("popularMovies");
            wsAT.addHeader("Content-Type", "application/json;charset=utf-8");
            wsAT.addParam("api_key", Constants.API_KEY);
            wsAT.addParam("sort_by", "popularity.desc");
            if(page != -1){
                wsAT.addParam("page", String.valueOf(page));
            }
            wsAT.execute();
        } catch (Exception e) {
            MyLog.e("WebServiceAT", "Error!", e);
        }
    }

    private void searchMovies(int page) {
        layoutLoading.setVisibility(View.VISIBLE);
        if(lastSearchAT != null){
            lastSearchAT.cancel(true);
        }
        WebServiceAT wsAT = new WebServiceAT(this,
                new WebServiceOnCompleteListener(),
                Constants.WS_URL_MOVIES_SEARCH,
                WebService.RequestMethod.GET);
        lastSearchAT = wsAT;
        try {
            wsAT.addName("searchMovies");
            wsAT.addHeader("Content-Type", "application/json;charset=utf-8");
            wsAT.addParam("api_key", Constants.API_KEY);
            wsAT.addParam("query", URLEncoder.encode(searchText, "utf-8"));
            if(page != -1){
                wsAT.addParam("page", String.valueOf(page));
            }
            wsAT.execute();
        } catch (Exception e) {
            MyLog.e("WebServiceAT", "Error!", e);
        }
    }

    private void downloadMovieImage(int id, String url, boolean isSearch) {
        DownloadImageAT imageAT = new DownloadImageAT(this, new DownloadImageOnCompleteListener());
        try {
            imageAT.setId(id);
            imageAT.setSearch(isSearch);
            imageAT.execute(url);
        } catch (Exception e) {
            MyLog.e("DownloadImageAT", "Error!", e);
        }
    }

    private void processWSresponse200(String response, boolean isSearch){
        try {
            JSONObject jsonObject = new JSONObject(response);
            int currentPage = jsonObject.getInt("page");
            if(isSearch && currentPage==1){
                searchMovies.clear();
            }
            int totalPages = jsonObject.getInt("total_pages");
            JSONArray resultsJSON = jsonObject.getJSONArray("results");
            for (int i = 0; i < resultsJSON.length(); i++) {
                JSONObject movieJSON = resultsJSON.getJSONObject(i);
                Movie movie = JSONdecoder.decodeResult(movieJSON,this);
                if(movie!=null) {
                    if (isSearch) {
                        searchMovies.add(movie);
                    } else {
                        popularMovies.add(movie);
                    }
                    if (movie.getPicture() != null) {
                        String imageURL = Constants.WS_URL_MOVIES_IMAGE + movie.getPicture();
                        downloadMovieImage(movie.getId(), imageURL, isSearch);
                    }
                }
            }
            if(isSearch) {
                searchCurrentPage = currentPage;
                searchTotalPages = totalPages;
                if(showSearch) {
                    updateMovies(searchMovies);
                    if(currentPage == 1) {
                        listOfMovies.setSelection(0);
                    }
                }
            }
            else{
                popularCurrentPage = currentPage;
                popularTotalPages = totalPages;
                if(!showSearch) {
                    updateMovies(popularMovies);
                }
            }
        } catch (JSONException e) {
            MyLog.e("WebService AT", "Parsing JSON Error!", e);
        }
    }

    private void processWSresponse401(String response){
        String errorMsg = JSONdecoder.decode401Response(response);
        if(errorMsg!=null){
            showDialogMessage(errorMsg);
        }
        else{
            showDialogMessage(getResources().getString(R.string.authentication_error));
        }
    }

    public class WebServiceOnCompleteListener implements WebServiceCompleteListener {

        @Override
        public void onTaskComplete(WebServiceResult result) {
            // process response of the Web Service
            int statusCode = result.getStatus();
            String response = result.getResponse();
            String name = result.getName();
            if(statusCode == 0){
                // No connection
                showDialogMessage(getResources().getString(R.string.no_connection_title) + "\n" + getResources().getString(R.string.no_connection_message));
            }
            else if(statusCode == 200){
                boolean isSearch = false;
                if(name!=null && name.equalsIgnoreCase("searchMovies")){
                    isSearch = true;
                }
                processWSresponse200(response,isSearch);
            }
            else if(statusCode == 401){
                processWSresponse401(response);
            }
            else{
                showDialogMessage(getResources().getString(R.string.server_not_available));
            }
            needsRefresh = true;
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public class DownloadImageOnCompleteListener implements DownloadImageCompleteListener {

        @Override
        public void onTaskComplete(DownloadImageResult result) {
            int resultId = result.getId();
            boolean isSearch = result.isSearch();
            if(isSearch) {
                int size = searchMovies.size();
                for (int i = size - 1; i >= 0; i--) {
                    int movieId = searchMovies.get(i).getId();
                    if (resultId == movieId) {
                        Bitmap bitmap = result.getBitmap();
                        searchMovies.get(i).setBitmap(bitmap);
                        if(showSearch) {
                            updateMovies(searchMovies);
                        }
                        break;
                    }
                }
            }
            else{
                int size = popularMovies.size();
                for (int i = size - 1; i >= 0; i--) {
                    int movieId = popularMovies.get(i).getId();
                    if (resultId == movieId) {
                        Bitmap bitmap = result.getBitmap();
                        popularMovies.get(i).setBitmap(bitmap);
                        if(!showSearch) {
                            updateMovies(popularMovies);
                        }
                        break;
                    }
                }
            }
        }
    }
}
