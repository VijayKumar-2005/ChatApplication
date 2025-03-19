package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context,val messageList : ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1) {
            val view : View = LayoutInflater.from(context).inflate(R.layout.recieve,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentmessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentmessage.senderId)) {
            return ITEM_SENT
        }else{
            return ITEM_RECIEVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentmessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java) {
            val currentmessage = messageList[position]
            val viewHolder = holder as SentViewHolder
            holder.sentmessage.text = currentmessage.message
        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.recivemessage.text = currentmessage.message
        }
    }
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentmessage = itemView.findViewById<TextView>(R.id.sent_txt_mess)

    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recivemessage = itemView.findViewById<TextView>(R.id.recv_txt_mess)

    }

}