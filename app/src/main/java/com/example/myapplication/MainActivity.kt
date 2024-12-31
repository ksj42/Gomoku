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
import android.view.MenuItem
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.navigation.NavOptions
import com.example.myapplication.ui.gallery.GalleryFragment

class MainActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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
        // 자동으로 각 네비게이션 타이틀을 앱바에 출력하는 코드
        // 왼쪽에 삼선 표기 기능
        setupActionBarWithNavController(navController, appBarConfiguration)
        // 네비게이션의 버튼 기능 활성화
        // 각 아이템을 클릭했을때 액티비티 활성화
        navView.setupWithNavController(navController)

        // 이코드가 있어야 프래그먼트 그래프가 작종함
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                }
                R.id.nav_gallery -> {
                    navController.navigate(R.id.nav_gallery)
                }
                R.id.nav_slideshow -> {
                    navController.navigate(R.id.nav_slideshow)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START) // Drawer 닫기
            true
        }
    }

    // 오른쪽 위 세팅 아이콘 기능
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // 프래그먼트 그래프 설정 코드
    // 없으면 메뉴 클릭해도 반응없음
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }
}