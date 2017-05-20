package hkapps.playmxtv.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.SpeechRecognitionCallback;
import android.util.Log;

import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import hkapps.playmxtv.Activities.MainActivity;
import hkapps.playmxtv.Activities.SearchActivity;
import hkapps.playmxtv.Adapters.CardPresenter;
import hkapps.playmxtv.Liestener.ResultsListener;
import hkapps.playmxtv.Model.Ficha;
import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;
import hkapps.playmxtv.Services.PlayMaxAPI;
import hkapps.playmxtv.Services.Requester;
import hkapps.playmxtv.Static.Utils;

public class SearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider,
        Response.Listener<String>{

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final int REQUEST_SPEECH = 0x00000010;
    private static final boolean FINISH_ON_RECOGNIZER_CANCELED = true;
    private ArrayObjectAdapter mRowsAdapter;
    private boolean mResultsFound;
    private Usuario mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        mUser = (Usuario) getActivity().getIntent().getSerializableExtra(MainActivity.USER);

        setSearchResultProvider(this);
        setOnItemViewClickedListener(new ResultsListener(this.getActivity()));

        if (hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            // SpeechRecognitionCallback is not required and if not provided recognition will be handled
            // using internal speech recognizer, in which case you must have RECORD_AUDIO permission
            setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
                @Override
                public void recognizeSpeech() {
                    Log.v(TAG, "recognizeSpeech");
                    try {
                        startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Cannot find activity for speech recognizer", e);
                    }
                }
            });
        }
    }

    public static boolean hasPermission(final Context context, final String permission) {
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(
                permission, context.getPackageName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SPEECH:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setSearchQuery(data, true);
                        break;
                    default:
                        // If recognizer is canceled or failed, keep focus on the search orb
                        if (FINISH_ON_RECOGNIZER_CANCELED) {
                            if (!hasResults()) {
                                getView().findViewById(R.id.lb_search_bar_speech_orb).requestFocus();
                            }
                        }
                        break;
                }
                break;
        }
    }

    public boolean hasResults() {
        return mRowsAdapter.size() > 0 && mResultsFound;
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        Log.i(TAG, String.format("Search Query Text Change %s", newQuery));
        search(newQuery);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, String.format("Search Query Text Submit %s", query));
        search(query);
        return true;
    }

    private void search(String query){
        Requester.request(this.getActivity(),PlayMaxAPI.getInstance().requestSearch(mUser, query),this);
    }

    @Override
    public void onResponse(String response) {
        try {
            List<Ficha> fichas = Ficha.listFromXML(response);
            ArrayObjectAdapter res = new ArrayObjectAdapter(new CardPresenter());

            res.addAll(0,fichas);

            HeaderItem header = new HeaderItem("Resultados");
            mRowsAdapter.clear();
            ListRow row = new ListRow(header, res);
            mRowsAdapter.add(row);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
