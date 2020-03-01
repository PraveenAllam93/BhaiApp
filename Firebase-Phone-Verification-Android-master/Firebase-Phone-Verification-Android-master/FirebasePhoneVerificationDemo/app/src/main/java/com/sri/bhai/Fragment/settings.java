package com.sri.bhai.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sri.bhai.Main2Activity;
import com.sri.bhai.R;
import com.sri.bhai.shared.SharedPrefManager;

/**
 * Created by Belal on 18/09/16.
 */


public class settings extends Fragment {

    ImageView edit,edit_msg;
    EditText msgtext;
    TextView number,seek;
    private static int REQUEST_OVERLAY_PERMISSION=12;
    private static int SYSTEM_WINDOW=12;

    @Nullable
    @Override


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        final View v = inflater.inflate(R.layout.settings, container, false);
        //getActivity().setTitle("SETTINGS");
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("SETTINGS");

        //canDrawOverlays(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Settings.ACTION_PRIVACY_SETTINGS
            if (!Settings.canDrawOverlays(getContext())) {
                // ask for setting
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
            }
        }


        SeekBar sk = (SeekBar) v.findViewById(R.id.seekBar);
        edit_msg = (ImageView) v.findViewById(R.id.edit_msg);
        number = (TextView) v.findViewById(R.id.number);
        msgtext = (EditText) v.findViewById(R.id.msgtext);
        seek = (TextView) v.findViewById(R.id.textView4);
        sk.setProgress(Integer.parseInt(SharedPrefManager.getInstance(getContext()).getdist()));
        seek.setText(SharedPrefManager.getInstance(getContext()).getdist() + "Mtr.");
        number.setText(SharedPrefManager.getInstance(getContext()).getpolice());
        msgtext.setText(SharedPrefManager.getInstance(getContext()).getmsg());
        Log.e("police", "" + Integer.parseInt(SharedPrefManager.getInstance(getContext()).getdist()));
//        Log.e("number", "" + Integer.parseInt(SharedPrefManager.getInstance(getContext()).getpolice()));
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Emergency Number");
        //comments by ksg
//        number.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final EditText input = new EditText(getContext());
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        number.setText(input.getText().toString());
//                        SharedPrefManager.getInstance(getContext()).savepolice(input.getText().toString());
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
//            }
//        });
//comments by ksg
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final EditText input = new EditText(getContext());
//                    input.setInputType(InputType.TYPE_CLASS_TEXT);
//                    builder.setView(input);
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        number.setText(input.getText().toString());
//                        SharedPrefManager.getInstance(getContext()).savepolice(input.getText().toString());
//                     }
//                  });
//                 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
//            }
//        });
        final AlertDialog.Builder builderr = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        builderr.setTitle("Emergency Message");
        msgtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builderr.setView(input);
                builderr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().contentEquals("")) {
                            Toast.makeText(getContext(), "Please Choose Message", Toast.LENGTH_LONG).show();
                        } else {
                            msgtext.setText(input.getText().toString());
                            SharedPrefManager.getInstance(getContext()).savemsg(input.getText().toString());

                        }

                    }
                });
                builderr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builderr.show();
            }
        });
        //commented by ksg
//        edit_msg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final EditText input = new EditText(getContext());
//                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                builderr.setView(input);
//                builderr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (input.getText().toString().contentEquals("")) {
//                            Toast.makeText(getContext(),"Please Choose Message",Toast.LENGTH_LONG).show();
//                        } else {
//                            msgtext.setText(input.getText().toString());
//                            SharedPrefManager.getInstance(getContext()).savemsg(input.getText().toString());
//
//                        }
//
//                    }
//                });
//                builderr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builderr.show();
//            }
//        });
//        sk.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        sk.setEnabled(false);
///*
//        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//                seek.setText(String.valueOf(i)+"Mtr.");
//                SharedPrefManager.getInstance(getContext()).savedist(String.valueOf(i));
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        */
     return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Settings");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getContext())) {
                    // permission granted...
                }else{
                    // permission not granted...
                }
            }
        }
    }
    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) return true;
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) return false; //getSystemService might return null
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}