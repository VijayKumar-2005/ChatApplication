package com.example.chatapplication

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class ChatActivity : AppCompatActivity() {

    private lateinit var messageReceiver: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var firestore: FirebaseFirestore
    private var receiveRoom: String? = null
    private var sendRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        if (senderUid == null || receiverUid == null) {
            finish()
            return
        }

        supportActionBar?.title = name

        sendRoom = senderUid + receiverUid
        receiveRoom = receiverUid + senderUid

        firestore = FirebaseFirestore.getInstance()
        messageReceiver = findViewById(R.id.chatRecycleView)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.sendButton)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        messageReceiver.adapter = messageAdapter
        messageReceiver.layoutManager = LinearLayoutManager(this)
        val colors = listOf(
            ContextCompat.getColor(this, R.color.blue),
            ContextCompat.getColor(this, R.color.pink)
        )
        val randomColor = colors.random()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(randomColor))
        firestore.collection("chats")
            .document(sendRoom!!)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    messageList.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                    messageReceiver.scrollToPosition(messageList.size - 1)
                }
            }

        sendButton.setOnClickListener {
            val message = messageBox.text.toString().trim()
            if (message.isNotBlank()) {
                val messageObject = Message(message, senderUid)
                val messageData = hashMapOf(
                    "message" to messageObject.message,
                    "senderId" to messageObject.senderId,
                    "timestamp" to FieldValue.serverTimestamp()
                )

                firestore.collection("chats")
                    .document(sendRoom!!)
                    .collection("messages")
                    .add(messageData)
                    .addOnSuccessListener {
                        firestore.collection("chats")
                            .document(receiveRoom!!)
                            .collection("messages")
                            .add(messageData)
                    }

                messageBox.setText("")
            }
        }
    }
}
