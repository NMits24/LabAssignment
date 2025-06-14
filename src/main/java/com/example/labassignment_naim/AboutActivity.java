package com.example.labassignment_naim;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem; // Import for onOptionsItemSelected
import android.widget.TextView;

import androidx.appcompat.app.ActionBar; // Import for ActionBar
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Enable the Up button (back arrow in action bar)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About"); // You can set a title here
        }

        // Find the TextView for the GitHub URL
        TextView githubUrlTextView = findViewById(R.id.aboutGitHubUrl);

        // Make the URL clickable
        githubUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // This method handles clicks on the Up button in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // This simulates pressing the system back button
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}