package com.example.empty.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.empty.R

class ChatMessageAdapter(private val context: Context, private var messages: List<ChatMessage>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount() = messages.size

    override fun getItem(position: Int) = messages[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val message = messages[position]
        val view = convertView ?: inflater.inflate(R.layout.item_chat_message, parent, false)
        val messageText = view.findViewById<TextView>(R.id.messageText)
        messageText.text = message.text

        // Optionally adjust alignment or styling based on sender:
        if (message.sender == Sender.USER) {
            // Align text to right, for example.
            messageText.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        } else {
            messageText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
        return view
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
