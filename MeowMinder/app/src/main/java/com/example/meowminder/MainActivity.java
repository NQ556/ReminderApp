package com.example.meowminder;

import static com.example.meowminder.AddNote.CHANNEL_ID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Create notification channel
        createNotificationChannel();

        //Initialize
        initializeUI();

        //View pager
        setUpViewPager();

        //Bottom navigation
        selectOption();
    }

    private void initializeUI() {
        viewPager2 = (ViewPager2) findViewById(R.id.view_pager);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        //Select icon in navigation when swipe to another layout
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position)
                {
                    case 0:
                        bottomNav.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.calendar).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.notification).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.setting).setChecked(true);
                        break;
                }
            }
        });
    }

    private void selectOption() {
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.calendar:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.notification:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.setting:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel_1";
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}