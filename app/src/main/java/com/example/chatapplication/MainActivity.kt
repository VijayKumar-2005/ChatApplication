package com.example.chatapplication

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var userRecycler: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val colors = listOf(
            ContextCompat.getColor(this, R.color.blue),
            ContextCompat.getColor(this, R.color.pink)
        )
        val randomColor = colors.random()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(randomColor))

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        userRecycler = findViewById(R.id.userRecycler)
        progressBar = findViewById(R.id.progressBar)

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = adapter

        val currentUserId = auth.currentUser?.uid

        listenerRegistration = firestore.collection("users")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MainActivity", "Firestore error", exception)
                    progressBar.visibility = View.GONE
                    return@addSnapshotListener
                }

                snapshot?.let {
                    userList.clear()
                    for (document in it.documents) {
                        val user = document.toObject(User::class.java)
                        if (user != null && user.uid != currentUserId) {
                            userList.add(user)
                        }
                    }
                    userList.sortBy { it.name?.lowercase() ?: "" }
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                    userRecycler.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            auth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
