package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdaptor extends RecyclerView.Adapter<ToDoAdaptor.ViewHolder> {

    private static final String TAG =   ToDoAdaptor.class.getSimpleName();

    private Context context;

    MainActivity mainActivity = new MainActivity();

    List<ToDo> toDoList = new ArrayList<ToDo>();

    OnCallBack callback;

    int type = 0;

    public ToDoAdaptor(Context context,List<ToDo> toDoList ,int type) {

        this.context = context;
        this.toDoList = toDoList;
        this.type  = type;

        if (context instanceof OnCallBack) {

            callback        =   (OnCallBack) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


      //  ViewHolder viewHolder;

        View view = null;

        if(type == 1){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todo, parent, false);

        }else if(type == 2){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_delete_todo, parent, false);

        }

       // view = LayoutInflater.from(context).inflate(R.layout.list_todo,null);

        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

         final ToDo files = toDoList.get(position);

         holder.bind(files, position);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(type ==2){

                            callback.deleteList(files);

                        }


                    }
                });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        CheckBox checkBox ;

        public ViewHolder(View itemView) {

            super(itemView);

            title      =   itemView.findViewById(R.id.title);

            checkBox   =   itemView.findViewById(R.id.todo_checkbox);

        }

        public void bind(final ToDo files, int position) {

            title.setText(files.getTask());

            if(type == 2){

                title.setTextColor(R.color.gray);
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(type == 1){

                        callback.updateList(files);

                        Toast.makeText(context,"Deleted..!",Toast.LENGTH_SHORT).show();

                    }else {

                        callback.deleteList(files);

                    }



                }
            });

        }
    }

    public void update(List<ToDo> toDoList)
    {
        this.toDoList = toDoList;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {

        return toDoList.size();

    }

    public interface OnCallBack {

        void updateList(ToDo toDo);

        void deleteList(ToDo toDo);
    }
 }
