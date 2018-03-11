package com.example.vaibhav.iot.utilities;

/**
 * Created by f71ud on 05/10/2017.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    int GPIO_NO;
    String response = "";
    TextView textResponse;
    int task ;
    BufferedReader r;
    PrintWriter w;


    public Client(String addr, int GPIO_NO, int task) {
        dstAddress = addr;
        dstPort = 3000;
        this.GPIO_NO = GPIO_NO;
        this.task = task;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            w = new PrintWriter(socket.getOutputStream(), true);
            w.println(GPIO_NO+" "+task);



        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i("SOcket","Socket Connection operation Complete");
        super.onPostExecute(result);
    }

}