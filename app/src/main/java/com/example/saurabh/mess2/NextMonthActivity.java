package com.example.saurabh.mess2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.saurabh.mess2.MainActivity.BATCH;
import static com.example.saurabh.mess2.MainActivity.BUFFER;
import static com.example.saurabh.mess2.MainActivity.BUFFER_GRPID;
import static com.example.saurabh.mess2.MainActivity.PAID_NEXT;
import static com.example.saurabh.mess2.MainActivity.UserDataObj;
import static com.example.saurabh.mess2.MainActivity.connected;

public class NextMonthActivity extends AppCompatActivity {

    Button PaymentStatusBtn,mAddGrpBtn;
    String BuffGrpId;
    private TextView UserGroupIdTxtView;
    private static TextView memberTxtView[]=new TextView[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_month);

        setTitle("Upcoming Month Details");


        mAddGrpBtn=(Button)findViewById(R.id.AddGroupBtn);

        memberTxtView[0] = (TextView)findViewById(R.id.member1TxtView);
        memberTxtView[1] = (TextView)findViewById(R.id.member2TxtView);
        memberTxtView[2] = (TextView)findViewById(R.id.member3TxtView);


        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("buffgroupid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               BuffGrpId= dataSnapshot.getValue().toString();
                PaymentStatusBtn=(Button)findViewById(R.id.PaymentStatusBtn);

                if(PAID_NEXT.equals("not paid"))
                {
                    PaymentStatusBtn.setText("Tap to Pay for Next Month");
                }
                else if(PAID_NEXT.equals("paid")&&BuffGrpId.equals("not paid"))
                {
                    PaymentStatusBtn.setText("Tap to Confirm Payment");
                }
                else
                {
                    PaymentStatusBtn.setText("Payment Confirmed!");
                }

              //  setOnclick();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PaymentStatusBtn=(Button)findViewById(R.id.PaymentStatusBtn);
        PaymentStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PAID_NEXT.equals("not paid"))
                {

                    Log.v("E_VALUE","PAID_NEXT"+PAID_NEXT);
                    Intent PaymentIntent2=new Intent(NextMonthActivity.this,PaymentActivity.class);
                    PaymentIntent2.putExtra("UserID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(PaymentIntent2);
                }
                else if(PAID_NEXT.equals("paid")&&BuffGrpId.equals("not paid"))
                {
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("batch").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            BATCH=dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if(BATCH.equals("not paid"))
                    {
                        String from="NextMonth";
                        SubPage02 obj = new SubPage02();
                        obj.setbatch(from);
                    }
                    else
                    {
                        String from="NextMonth";
                        SubPage02 obj = new SubPage02();
                        obj.onPayClicked(from);
                    }
                    PaymentStatusBtn.setText("Tap to Confirm Payment");
                }
                else
                {
                    PaymentStatusBtn.setText("Payment Confirmed!");
                }
            }
        });




        mAddGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  if(isConnected()) {

                final String[] BufferGroupId = new String[1];
                if(isConnected())
                {
                    Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(20);
                    final AddFriendHandler mAddFriendHandler = new AddFriendHandler(NextMonthActivity.this);

                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("buffgroupid").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            BufferGroupId[0] =dataSnapshot.getValue().toString();
                            if(!BufferGroupId[0].equals("not paid")&& !PAID_NEXT.equals("not paid"))
                            {

                                mAddFriendHandler.showDialogMethod();
                            }
                            else
                            {
                                mAddFriendHandler.showNotPaidDialogue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
                else
                {
                    Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 75,100,75};

                    // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                    v.vibrate(pattern, -1);
                    Toast.makeText(getBaseContext(),"No Internet! Please Check your Connection",Toast.LENGTH_LONG).show();
                }

            }
        });

        initialiseTextViews();

    }

    private void initialiseTextViews() {

        final String[] buffer = new String[1];


        if(!BATCH.equals("not paid")) {
            FirebaseDatabase.getInstance().getReference().child(BATCH).child("buffer")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            buffer[0] = dataSnapshot.getValue().toString();
                            setTextViews(buffer[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void setTextViews(String Buffer) {


        UserGroupIdTxtView = (TextView) findViewById(R.id.userGroupID);
        if(BUFFER_GRPID.equals("not paid"))
        {
            UserGroupIdTxtView.setText("Pay to generate Group Code");

        }
        else {

            UserGroupIdTxtView.setText("YOUR GROUP CODE: "+BUFFER_GRPID);
        }

        try {


            Log.v("E_VALUE","CODE RUN 2");


            // if(memberTxtView[0]!=null)
            memberTxtView[0] = (TextView)findViewById(R.id.member1TxtView);

            memberTxtView[0].setText("");
            // if(memberTxtView[1]!=null)
            memberTxtView[1] = (TextView)findViewById(R.id.member2TxtView);

            memberTxtView[1].setText("");
            //  if(memberTxtView[2]!=null)
            memberTxtView[2] = (TextView)findViewById(R.id.member3TxtView);

            memberTxtView[2].setText("");

        }
        catch (Exception e)
        {
            Log.v("E_VALUE","CODE RUN 3");

        }




        DatabaseReference mInCurGrpMemberId=FirebaseDatabase.getInstance().getReference().child(Buffer).child(BUFFER_GRPID)
                .child("memberid");



        mInCurGrpMemberId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int[] i = {0};
                for(DataSnapshot dsp : dataSnapshot.getChildren())
                {


                    FirebaseDatabase.getInstance().getReference().child("users").child(dsp.getKey())
                            .child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            try {
                                if (!dataSnapshot.getValue().toString().equals(UserDataObj.getName())) {
                                    memberTxtView[2] = (TextView) findViewById(R.id.member3TxtView);
                                    memberTxtView[1] = (TextView) findViewById(R.id.member2TxtView);
                                    memberTxtView[0] = (TextView) findViewById(R.id.member1TxtView);
                                    memberTxtView[i[0]].setText((i[0] + 1) + ". " + dataSnapshot.getValue().toString());
                                    i[0]++;
                                }
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(NextMonthActivity.this,"Exception caught 2",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

    private void setOnclick() {
        PaymentStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PAID_NEXT.equals("not paid"))
                {
                    Intent PaymentIntent=new Intent(NextMonthActivity.this,PaymentActivity.class);
                     startActivity(PaymentIntent);
                }
                else if(PAID_NEXT.equals("paid")&&BuffGrpId.equals("not paid"))
                {
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("batch").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            BATCH=dataSnapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if(BATCH.equals("not paid"))
                    {
                        SubPage02 obj = new SubPage02();
                        obj.setbatch("NextMonth");
                    }
                    else
                    {
                        SubPage02 obj = new SubPage02();
                        obj.onPayClicked("NextMonth");
                    }
                    PaymentStatusBtn.setText("Tap to Confirm Payment");
                }
                else
                {
                    PaymentStatusBtn.setText("Payment Confirmed!");
                }
            }
        });



    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected=true;
            return true;
        }
        else
            connected = false;
        return false;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}
