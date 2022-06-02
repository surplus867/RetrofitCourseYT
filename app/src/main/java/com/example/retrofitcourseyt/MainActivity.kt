package com.example.retrofitcourseyt

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitcourseyt.databinding.ActivityMainBinding
import com.example.retrofitcourseyt.databinding.PostLayoutDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRv()
        getAllPosts()

        binding.fabAddPost.setOnClickListener {
            //addPostDialog()
            lifecycleScope.launchWhenCreated {
                RetrofitInstance.retrofit.deletePost()
                val response = RetrofitInstance.retrofit.deletePost()

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Fake Post Deleted", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.d("error", "Error when delete post: ${
                        response.code()
                    }")
                }
            }
        }
    }

    private fun setupRv() {
        postAdapter = PostAdapter()
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = postAdapter
            setHasFixedSize(true)
        }
    }

    private fun getAllPosts() {
        lifecycleScope.launchWhenCreated {
            val response = RetrofitInstance.retrofit.getAllPosts()

            if (response.isSuccessful && response.body() != null) {

                postAdapter.differ.submitList(response.body())

                Log.d("response", "getAllPosts: ${response.body()}")

            } else {

                Toast.makeText(
                    this@MainActivity,
                    "Error Code: ${response.code()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //posting something into server using retrofit2

    private fun addPostDialog() {
        val mDialog = Dialog(this)
        val mBinding = PostLayoutDialogBinding.inflate(layoutInflater)
        mDialog.setContentView(mBinding.root)

        mBinding.btnCancel.setOnClickListener {
            mDialog.dismiss()
        }

        mBinding.btnPost.setOnClickListener {

            if (mBinding.etPostTitle.text.toString()
                    .isNotEmpty() && mBinding.etBodyPost.text.toString().isNotEmpty()
            ) {

                val title = mBinding.etPostTitle.text.toString()
                val body = mBinding.etBodyPost.text.toString()
                val userId = mBinding.etUserId.text.toString().toInt()

                makePost(title, body, userId)
                Toast.makeText(
                    this,
                    "Post done successfully",
                    Toast.LENGTH_SHORT
                ).show()
                mDialog.dismiss()
            } else {
                Toast.makeText(
                    this,
                    "Title and Body can't be empty", Toast.LENGTH_SHORT
                ).show()
            }

        }

        mDialog.show()
    }

    private fun makePost(title: String, body: String, userId: Int) {

        lifecycleScope.launchWhenCreated {
            val post = PostItem(body, 0, title, userId)

            val response = RetrofitInstance.retrofit.posting(post)

            if (response.isSuccessful && response.body() != null) {
                Log.d("post response", "OurPost: ${response.body()}")
            } else {
                Log.d("post Error", "Error: $(response.code()}")
            }
        }
    }
}