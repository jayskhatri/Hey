package com.jayskhatri.hey.hey;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jayskhatri.hey.hey.Utils.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Chat extends AppCompatActivity implements View.OnClickListener{

    public ArrayList<Conversation> convList;

    private ChatAdapter adp;
    private TextView buddyName;
    private ImageView backBtn;

    private EditText txt;
    private String buddy;
    private Date lastmsgDate;

    private boolean isRunning;
    private static Handler handler;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        convList= new ArrayList<Conversation>();
        ListView list=findViewById(R.id.list);
        adp=new ChatAdapter();
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);

        txt=findViewById(R.id.txt);
        buddyName=findViewById(R.id.buddyName);
        backBtn=findViewById(R.id.backBtn);
        txt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        sendBtn=findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        backBtn.setOnClickListener(this);

        buddy=getIntent().getStringExtra(Const.EXTRA_DATA);

        buddyName.setText(buddy);



    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning=true;
        loadConversationList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning=false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendBtn){
            sendMessage();
        }
        else if(view.getId()==R.id.backBtn){
            finish();
        }
    }

    private void sendMessage()
    {
        if(txt.length()==0)
            return;

        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(),0);

        String s = txt.getText().toString();
        final Conversation c = new Conversation(s,new Date(),UserList.user.getUsername());
        c.setStatus(Conversation.STATUS_SENDING);
        convList.add(c);
        adp.notifyDataSetChanged();
        txt.setText(null);

        ParseObject po=new ParseObject("Chat");
        po.put("sender",UserList.user.getUsername());
        po.put("receiver",buddy);
        po.put("message",s);
        po.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e == null)
                {
                    c.setStatus(Conversation.STATUS_SENT);
                }
                else
                    c.setStatus(Conversation.STATUS_FAILED);

                adp.notifyDataSetChanged();
            }
        });
    }

    private void loadConversationList(){

        ParseQuery<ParseObject> q= ParseQuery.getQuery("Chat");
        if(convList.size()==0){
            //load all messages
            ArrayList<String> al = new ArrayList<String>();
            al.add(buddy);
            al.add(UserList.user.getUsername());
            q.whereContainedIn("sender",al);
            q.whereContainedIn("receiver",al);
        }
        else {
            //load only newly received messages
            if(lastmsgDate !=null){
                q.whereGreaterThan("CreatedAt",lastmsgDate);
                q.whereEqualTo("sender",buddy);
                q.whereEqualTo("receiver",UserList.user.getUsername());
            }
        }
        q.orderByAscending("CreatedAt");
        q.setLimit(30);
        q.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> li, ParseException e) {
                if(li!=null && li.size()>0){
                    for(int i=li.size() - 1;i>=0;i--){
                        ParseObject po= li.get(i);
                        Conversation c=new Conversation(po.getString("message"),po.getCreatedAt(),po.getString("sender"));
                        convList.add(c);
                        if(lastmsgDate==null || lastmsgDate.before(c.getDate()))
                            lastmsgDate = c.getDate();
                        adp.notifyDataSetChanged();
                    }
                }
            }
        });

    }
    private class ChatAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return convList.size();
        }

        @Override
        public Conversation getItem(int i) {
            return convList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int pos, View v, ViewGroup arg2) {

            Conversation c= getItem(pos);
            if(c.isSent())
                v= getLayoutInflater().inflate(R.layout.chat_item_sent,null);
            else
                v=getLayoutInflater().inflate(R.layout.chat_item_receive,null);

            TextView lbl=v.findViewById(R.id.lbl1);
            lbl.setText(DateUtils.getRelativeDateTimeString(Chat.this,c.getDate().getTime(),DateUtils.SECOND_IN_MILLIS,DateUtils.DAY_IN_MILLIS,0));

            lbl=v.findViewById(R.id.lbl2);
            lbl.setText(c.getMsg());

            lbl=v.findViewById(R.id.lbl3);
            if(c.isSent())
            {
                if(c.getStatus()== Conversation.STATUS_SENT)
                    lbl.setText("Delievered");
                else if (c.getStatus()== Conversation.STATUS_SENDING)
                    lbl.setText("Sending...");
                else if(c.getStatus()==Conversation.STATUS_FAILED)
                    lbl.setTextColor(Color.RED);
                    lbl.setText("Failed");
            }
            else
                lbl.setText("");

            return v;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

