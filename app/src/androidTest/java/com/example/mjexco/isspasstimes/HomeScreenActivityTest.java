package com.example.mjexco.isspasstimes;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mjexco.isspasstimes.ui.HomeScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test class for HomeScreenActivity
 */

@MediumTest
@RunWith(AndroidJUnit4.class)
public class HomeScreenActivityTest {
    @Rule
    public ActivityTestRule<HomeScreenActivity> rule  = new  ActivityTestRule<>(HomeScreenActivity.class);

    @Test
    public void testUiComponentsPresent(){
        HomeScreenActivity activity = rule.getActivity();

        //check that recycler view component is not null
        View recyclerView = activity.findViewById(R.id.pass_times_list);
        assertThat(recyclerView, notNullValue());
        assertThat(recyclerView, instanceOf(RecyclerView.class));

        //check that text view component is not null
        View textView = activity.findViewById(R.id.status_text);
        assertThat(textView, notNullValue());
        assertThat(textView, instanceOf(TextView.class));
    }

    @Test
    public void testUiComponentsVisibility(){
        HomeScreenActivity activity = rule.getActivity();

        //check that recycler view component is hidden by default
        View recyclerView = activity.findViewById(R.id.pass_times_list);
        assertEquals(View.GONE, recyclerView.getVisibility());

        //check that text view component is visible by default
        View textView = activity.findViewById(R.id.status_text);
        assertEquals(View.VISIBLE, textView.getVisibility());
    }
}
