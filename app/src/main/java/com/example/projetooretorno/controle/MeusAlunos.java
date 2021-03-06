package com.example.projetooretorno.controle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.projetooretorno.R;
import com.example.projetooretorno.adapter.MeusAlunosAdapter;
import com.example.projetooretorno.helper.Conexao;
import com.example.projetooretorno.helper.ProfessorFirebase;
import com.example.projetooretorno.helper.RecyclerItemClickListener;
import com.example.projetooretorno.modelo.Matricula;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeusAlunos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Matricula> matriculas = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_alunos);

        recyclerView = findViewById(R.id.recyclerViewMeusAlunos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        user = ProfessorFirebase.getProfessorAtual();
        listarMatriculas();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Matricula m = matriculas.get(position);
                Intent i = new Intent(getBaseContext(), EditarMeusAlunos.class);
                i.putExtra("matriculaSelecionada", m);
                startActivity(i);
            }
            @Override
            public void onLongItemClick(View view, int position) {
                Matricula m = matriculas.get(position);
                Intent i = new Intent(getBaseContext(), EditarMeusAlunos.class);
                i.putExtra("matriculaSelecionada", m);
                startActivity(i);
            }
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }));
    }

    public void listarMatriculas(){
        firebaseDatabase = Conexao.getFirebaseDatabase();
        databaseReference = firebaseDatabase.getReference();
        DatabaseReference pesquisarMatriculas = databaseReference.child("Professor").child(user.getUid()).child("Matricula");
        pesquisarMatriculas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String idMatricula = dataSnapshot.child("idMatricula").getValue().toString();
                    String idProfessor = dataSnapshot.child("idProfessor").getValue().toString();
                    String idAluno = dataSnapshot.child("idAluno").getValue().toString();
                    matriculas.add(new Matricula(idMatricula, idProfessor, idAluno));
                }
                MeusAlunosAdapter adapter = new MeusAlunosAdapter(matriculas, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}