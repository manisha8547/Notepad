package com.example.notepad;

import android.content.Context;
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

    public ToDoAdaptor(Context context,List<ToDo> toDoList) {

        this.context = context;
        this.toDoList = toDoList;

        if (context instanceof OnCallBack) {
            callback        =   (OnCallBack) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        ViewHolder viewHolder;

        View view;

        view = LayoutInflater.from(context).inflate(R.layout.list_todo,null);

        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

         final ToDo files = toDoList.get(position);
         holder.bind(files, position);

            /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick:RESULT "+files.getFilePath());
                        callback.addPdfFile(files);

                    }
                });
        */

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        CheckBox checkBox ;

        public ViewHolder(View itemView) {

            super(itemView);

            title      =   itemView.findViewById(R.id.title);

            checkBox   =   itemView.findViewById(R.id.todo_checkbox);

        }

        public void bind( final ToDo files,int position) {

            title.setText(files.getTask());

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callback.deletelist(files);

                    Toast.makeText(context,"Deleted..!",Toast.LENGTH_SHORT).show();


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
        void deletelist(ToDo toDo);

    }
}
