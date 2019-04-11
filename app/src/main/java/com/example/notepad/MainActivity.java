package com.example.notepad;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ToDoAdaptor.OnCallBack{

    private RecyclerView listsView;

    private RecyclerView deteteListsView;

    LinearLayoutManager llm;

    LinearLayoutManager llm1;

    ImageButton addToDo;

    ToDoAdaptor adaptor ;

    ToDoAdaptor adaptorNew ;

  //  TextView blankListText;

    EditText edTodo ;

    List<ToDo> toDoList =new ArrayList<ToDo>();

    List<ToDo> deletedList =new ArrayList<ToDo>();

    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listsView           =   (RecyclerView) findViewById(R.id.todo_list);

        deteteListsView     =   (RecyclerView) findViewById(R.id.todo_delete_list);

        addToDo             =    findViewById(R.id.button);

       // blankListText       =    findViewById(R.id.blanklist_text);

        Database.initiate(MainActivity.this);

        addToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showPopUp();

            }
        });

        llm  = new LinearLayoutManager(MainActivity.this);

        llm1 = new LinearLayoutManager(MainActivity.this);

        listsView.setLayoutManager(llm);

        deteteListsView.setLayoutManager(llm1);


    }

    public void showPopUp(){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        edTodo = (EditText) dialogView.findViewById(R.id.edt_comment);

        Button button1       = (Button) dialogView.findViewById(R.id.buttonSubmit);

        ImageView microphone = (ImageView) dialogView.findViewById(R.id.btnSpeak);

        microphone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               ToDo toDo = new  ToDo();
               if(!edTodo.getText().toString().equals("")){

                   toDo.setTask(edTodo.getText().toString());
                   toDo.setStatus(0);
                   Database.addToDoItem(toDo);
                   dialogBuilder.dismiss();
                   onResume();

               } else {

                   Toast.makeText(MainActivity.this,"Enter Task..!",Toast.LENGTH_SHORT).show();
               }

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    /*** Showing google speech input dialog* */

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,  getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn\\'t support speech input",Toast.LENGTH_SHORT).show();
        }
    }

    /*** Receiving speech input* */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edTodo.setText(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        toDoList =Database.getList();

        deletedList = Database.getDeletedTask();

        Log.d("DELETEDD","=>"+deletedList.size() + " "+deletedList.size());

        if(adaptor == null){

            adaptor =   new ToDoAdaptor(MainActivity.this, toDoList,1);

            listsView.setAdapter(adaptor);


        } else {

            adaptor.update(toDoList);

            listsView.setAdapter(adaptor);

        }

        adaptorNew = new ToDoAdaptor(MainActivity.this,deletedList,2);

        deteteListsView.setAdapter(adaptorNew);


    }

    @Override
    public void updateList(ToDo toDo) {

        ContentValues values    =   new ContentValues();

        values.put("status", 1);

        Database.exeUpdateQuery("toDo",values,"id = "+toDo.getId());

       // Database.deleteQuery("toDo", "id = "+ toDo.getId());

        onResume();

    }

    @Override
    public void deleteList(final ToDo toDo) {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();

        dialogBuilder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.custom_delete_dialog, null);

        Button remove       = (Button) dialogView.findViewById(R.id.remove_button);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Database.deleteQuery("toDo", "id = "+ toDo.getId());
                dialogBuilder.dismiss();
                onResume();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


}
