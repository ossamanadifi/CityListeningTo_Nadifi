package com.example.citylisteningto_nadifi.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import com.example.citylisteningto_nadifi.services.NetworkChangeService
import com.example.citylisteningto_nadifi.R
import com.example.citylisteningto_nadifi.fragments.SongFragment
import com.example.citylisteningto_nadifi.fragments.AccountFragment
import com.example.citylisteningto_nadifi.fragments.HomeFragment
import com.example.citylisteningto_nadifi.fragments.MapFragment

class HomeView : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)
        supportActionBar?.title = "Home"


        val serviceIntent = Intent(this, NetworkChangeService::class.java)
        startService(serviceIntent)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        var toolbar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            supportActionBar?.title = "Home"
            }
            R.id.nav_Songs -> {supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SongFragment()).commit()
                supportActionBar?.title = "Songs"
            }
            R.id.nav_map -> {supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment()).commit()
                supportActionBar?.title = "Map"
            }
            R.id.nav_account -> {supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AccountFragment()).commit()
                supportActionBar?.title = "Account"
            }
            R.id.nav_logout -> {
                val intent = Intent(this, LoginView::class.java)
                startActivity(intent)
                finish()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}