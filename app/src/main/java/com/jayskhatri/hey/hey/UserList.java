package com.jayskhatri.hey.hey;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jayskhatri.hey.hey.Utils.Const;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserList extends Activity {

    private ArrayList<ParseUser> uList;

    public static ParseUser user;
    private ImageView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        user=ParseUser.getCurrentUser();

        logoutBtn=findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            //final ProgressDialog dia = ProgressDialog.show(getApplicationContext(),null,getString(R.string.alert_loading));
            @Override
            public void onClick(View view) {
                updateUserStatus(false);
                //dia.dismiss();
                ParseUser.getCurrentUser().logOut();
                Intent i=new Intent(UserList.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        updateUserStatus(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserList();
    }

    private void updateUserStatus(boolean online){
        user.put("online",online);
        user.saveEventually();
    }

    private void loadUserList(){
        final ProgressDialog dia = ProgressDialog.show(this,null,getString(R.string.alert_loading));
        ParseUser.getQuery().whereNotEqualTo("username",user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        dia.dismiss();
                        if(e==null){
                            if(objects.size()>0){
                                uList = new ArrayList<ParseUser>(objects);
                                ListView list =findViewById(R.id.listinUserList);
                                list.setAdapter(new UserAdapter());
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                                        //startActivity(new Intent(UserList.this,Chat.class).putExtra(Const.EXTRA_DATA),uList.get(pos).getUsername());
                                        Intent i=new Intent(UserList.this,Chat.class);
                                        i.putExtra(Const.EXTRA_DATA,uList.get(pos).getUsername());
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    private class UserAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return uList.size();
        }

        @Override
        public ParseUser getItem(int i) {
            return uList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            if(v==null)
                v = getLayoutInflater().inflate(R.layout.chat_item,null);
            ParseUser c=getItem(pos);
            TextView lbl=(TextView) v;
            lbl.setText(c.getUsername());
            lbl.setCompoundDrawablesWithIntrinsicBounds(c.getBoolean("online") ? R.drawable.ic_action_online : R.drawable.ic_action_offline,0,R.drawable.ic_action_arrow,0);

            return v;
        }
    }

}
