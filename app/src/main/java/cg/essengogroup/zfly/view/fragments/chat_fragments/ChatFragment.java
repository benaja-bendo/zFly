package cg.essengogroup.zfly.view.fragments.chat_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cg.essengogroup.zfly.R;
import cg.essengogroup.zfly.controller.adapter.UtilisateurAdapter;
import cg.essengogroup.zfly.model.Message;
import cg.essengogroup.zfly.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> userArrayList;
    private ArrayList<User> users;
    private LinearLayoutManager manager;
    private UtilisateurAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference,refUser;

    private View root;
    private Context context;

    private ArrayList<String> listDesId=new ArrayList<>();
    private ArrayList<User> maNouvelleListUsers=new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_chat, container, false);
        context=getContext();

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference().child("chats");
        refUser=database.getReference().child("users");

        recyclerView=root.findViewById(R.id.recycleDiscussion);
        manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        userArrayList=new ArrayList<>();

        getDiscussion();

        return root;
    }

    private void getDiscussion(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Message message=new Message();

                    message.setEnvoyeur(String.valueOf(data.child("envoyer").getValue()));
                    message.setReceveur(String.valueOf(data.child("recevoir").getValue()));
                    message.setMessage(String.valueOf(data.child("message").getValue()));
                    message.setHeure(String.valueOf(data.child("heure").getValue()));

                    if (message.getEnvoyeur().equalsIgnoreCase(mUser.getUid())){
                        userArrayList.add(message.getReceveur());
                    }

                    if (message.getReceveur().equalsIgnoreCase(mUser.getUid())){
                        userArrayList.add(message.getEnvoyeur());
                    }
//                    readDiscussion();
                    readRecentDiscussionJustID();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readDiscussion(){
        users=new ArrayList<>();

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    User user=new User();

                    user.setImage(String.valueOf(data.child("image").getValue()));
                    user.setUser_id(String.valueOf(data.child("user_id").getValue()));
                    user.setPseudo(String.valueOf(data.child("pseudo").getValue()));
                    user.setApseudo(String.valueOf(data.child("Apseudo").getValue()));
                    user.setStatus(String.valueOf(data.child("status").getValue()));

                    for (String id : userArrayList){

                        if (user.getUser_id().equalsIgnoreCase(id)){

                            if (users.size() !=0){

                                for (int i=0; i<users.size();i++){
                                    if (!user.getUser_id().equalsIgnoreCase(users.get(i).getUser_id())){
                                        if (!users.contains(users.get(i))){
                                            users.add(user);
                                        }
                                    }
                                }
                               /* for (User userObject : users){

                                    if (!user.getUser_id().equalsIgnoreCase(userObject.getUser_id())){
                                        users.add(user);
                                    }

                                }*/

                            }else {
                                users.add(user);
                            }

                        }
                    }
                }

                adapter=new UtilisateurAdapter(context,users,true);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readRecentDiscussionJustID(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listDesId.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (!String.valueOf(data.child("recevoir").getValue()).equalsIgnoreCase(mUser.getUid())){

                        if (!listDesId.contains(String.valueOf(data.child("recevoir").getValue()))){
                            listDesId.add(String.valueOf(data.child("recevoir").getValue()));
                        }
                    }
                }
                getUserParRapportListId(listDesId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserParRapportListId(ArrayList<String> listDesId){
        for (int i=0;i<listDesId.size();i++){

            refUser.child(listDesId.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=new User();

                    user.setImage(String.valueOf(dataSnapshot.child("image").getValue()));
                    user.setUser_id(String.valueOf(dataSnapshot.child("user_id").getValue()));
                    user.setPseudo(String.valueOf(dataSnapshot.child("pseudo").getValue()));
                    user.setApseudo(String.valueOf(dataSnapshot.child("Apseudo").getValue()));
                    user.setStatus(String.valueOf(dataSnapshot.child("status").getValue()));

                    if (maNouvelleListUsers.contains(user)){
                        Log.e("TAG", "onDataChange:  "+user.getApseudo()+" "+maNouvelleListUsers.contains(user) );
                    }else {
                        Log.e("TAG", "onDataChange:  "+user.getApseudo()+" "+maNouvelleListUsers.contains(user) );
                    }

                    maNouvelleListUsers.add(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        adapter=new UtilisateurAdapter(context,maNouvelleListUsers,true);
        recyclerView.setAdapter(adapter);

    }

}
