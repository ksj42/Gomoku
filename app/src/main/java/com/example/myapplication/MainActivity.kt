package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

import android.widget.Button
import android.widget.TextView
import android.animation.ObjectAnimator
import android.content.Intent
import android.util.Log
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //디버깅용

    //디버깅용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //디버깅용
        // setContentView(R.layout.ai_play_view)
        //setContentView(binding.root.findViewById(id_textview_01))
        //디버깅용

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        // Code start

        // 버튼1 클릭 시 새로운 액티비티로 이동
        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            val intent = Intent(this, ActivityTamaguchiview::class.java)
            startActivity(intent)  // 새로운 액티비티 시작
        }
        // 버튼2 클릭 시 새로운 액티비티로 이동
        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            val intent = Intent(this, ActivityTwoplayerview::class.java)
            startActivity(intent) // 새로운 액티비티 시작
        }

        // 버튼3 클릭 시 새로운 액티비티로 이동
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            val intent = Intent(this, ActivityAiplayview::class.java)
            startActivity(intent)  // 새로운 액티비티 시작
        }


        //val textView = findViewById<TextView>(R.id.id_textview_01)  // TextView를 찾는 코드
//        if (textView != null) {
//            Log.e("MainActivity", "TextView를 찾을 수 없습니다.")
//        //textView.text = "새로운 텍스트"
//        } else {
//            Log.e("MainActivity", "TextView를 찾을 수 없습니다.")
//        }


        //Code finish
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
}