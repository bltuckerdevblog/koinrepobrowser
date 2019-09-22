package com.abnormallydriven.koinrepobrowser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.abnormallydriven.koinrepobrowser.databinding.ActivityMainBinding
import com.abnormallydriven.koinrepobrowser.repos.RepositoryListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RepositoryListFragment())
            .commit()
    }
}
