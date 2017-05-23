package hkapps.playmxtv.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hkapps.playmxtv.Model.Usuario;
import hkapps.playmxtv.R;

public class SearchActivity extends Activity {

    private Usuario mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mUser = (Usuario)getIntent().getSerializableExtra(MainActivity.USER);
    }

    @Override
    public boolean onSearchRequested() {
        Intent search = new Intent(this, SearchActivity.class);
        search.putExtra(MainActivity.USER, mUser);
        startActivity(search);
        return true;
    }
}
