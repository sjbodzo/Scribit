package scribit.sjbodzo.com.scribit;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class JournalEntries extends ListActivity {
    private PostsDataAccessObject postsTableDAO;
    public static final String PREFS_SETTINGS = "TheSettingsFileYall";
    private ListView lv;
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);
        lv = getListView();
        self = this;
        setupDAOAndCursorAdapter(); //hook app into stored DB of entries
        firstLaunchCheck(); //if this is the first app launch respond accordingly

        Button addEntryButton = (Button) findViewById(R.id.add_new_entry_button);
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchAdd = new Intent(getApplicationContext(), AddEntryWizard.class);
                startActivity(launchAdd);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                final Post item = (Post) parent.getItemAtPosition(position);
                /**view.animate().setDuration(200).translationX(140)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                //this.remove(item);
                                //adapter.notifyDataSetChanged();
                                Intent viewPostIntent = new Intent(self, EditPost.class);
                                self.startActivity(viewPostIntent);
                            }
                        });
                 **/
                Intent viewPostIntent = new Intent(self, ViewEntry.class);
                viewPostIntent.putExtra("postEntry", item);
                self.startActivity(viewPostIntent);
            }
        });

        //TODO: animation for loading when busy grabbing list
        //TODO: write onClick
        //TODO: animation for onClick
        //TODO: link to addJournalEntry page
    }

    public void setupDAOAndCursorAdapter() {
        postsTableDAO = new PostsDataAccessObject(this); //associate to current Context
        postsTableDAO.open(); //inherited method, sets DB ref

        //TODO: call method to check if first time app load

        //hook in Cursor adapter
        //TODO: put group by functionality into call here when view flips
        List<Post> posts = postsTableDAO.getAllOfThePostyThings();
        ArrayAdapter<Post> velocidaptor = new CustomPostAdapter(this, posts);
        this.setListAdapter(velocidaptor);
    }

    //TODO: incorporate first launch logic into startup screen of app?
    public void firstLaunchCheck() {
        SharedPreferences spRef = getSharedPreferences(PREFS_SETTINGS, 0);
        boolean isFirstTimeUser = spRef.getBoolean("pref_key_virginal_ux", true);
        if (isFirstTimeUser) {
            //populate initial challenges!
            final CustomPostAdapter adapter = (CustomPostAdapter) getListAdapter();
            Post newPost = postsTableDAO.createJournalPost("Foo 1", "insane post bruh", 23.2, 99.42, "", "January 2, 2010");
            adapter.add(newPost);
            newPost = postsTableDAO.createJournalPost("Foo 2", "insane aehkpost bruh", 23.2, 99.42, "", "February 22, 2010");
            adapter.add(newPost);
            newPost = postsTableDAO.createJournalPost("Foo 3", "insakjhkhjkhjkhne post bruh", 23.2, 99.42, "", "December 17, 2010");
            adapter.add(newPost);
            newPost = postsTableDAO.createJournalPost("Foo 4", "insakjhkjhkjhne post bruh", 23.2, 99.42, "", "November 14, 2010");
            adapter.add(newPost);
            adapter.notifyDataSetChanged();

            //set status to already visited
            SharedPreferences.Editor eddy = spRef.edit();
            eddy.putBoolean("pref_key_virginal_ux", false);
            eddy.apply();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.journal_entries, menu);
        return true;
    }

    /**
     * onResume & onPause are lifecycle management methods that help make sure
     * that the cursor refs are handled properly if/when user leaves this page
     * of the app for whatever reason (i.e: phone call received or back call to
     * Activity stack
     */

    @Override
    protected void onResume() {
        postsTableDAO.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        postsTableDAO.close();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.new_post_entry) {
            Intent i = new Intent(getApplicationContext(), AddEntryWizard.class);
            startActivity(i);
            return true;
        }
        else {}
        return super.onOptionsItemSelected(item);
    }
}
