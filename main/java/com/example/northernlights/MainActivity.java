package com.example.northernlights;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * The MainActivity is the Activity that is Run when our app is opened.
 * The entire contents of the app runs within this activity.
 * it inherits from CameraActivity which is an openCV library.
 */
public class MainActivity extends CameraActivity {

//Initializing variables used throughout the program.
    private final static String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private static BluetoothDevice mDevice;
    private Button mSendBN;
    private final static String MY_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static BluetoothSocket mSocket = null;
    private static String mMessage = "Stop";
    private static PrintStream sender;
    String message;
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat input, roi, mono, blur, thresh;

    TextView angleResult;

    Button biasButton;
    int bias;


    /**
     * The findBrick method searches through the paired devices
     * and creates a Device object when it finds the EV3 Brick.
     */
    private void findBrick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 101);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            Log.d("BLUETOOTH", "FOUND DEVICE: "+device.getName());
            if (device.getName().equals("EV3") || device.getName().equals("shadow"))
                this.mDevice = device;
                Log.d("BLUETOOTH", "FOUND BRICK");
        }
    }


    /**
     * The initBluetooth method is used to initialise bluetooth
     * on the device and check that it is working.
     */
    private void initBluetooth() {
        Log.d(TAG, "Checking Bluetooth...");
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
            mSendBN.setClickable(false);
        } else {
            Log.d(TAG, "Bluetooth supported");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mSendBN.setClickable(false);
            Log.d(TAG, "Bluetooth not enabled");
        } else {
            Log.d(TAG, "Bluetooth enabled");
        }
    }


    /**
     * the Create socket method is used to create a socket
     * from our bluetooth device. The socket will be used for
     * bluetooth communications.
     * @throws IOException as the socket might throw an error.
     */
    public void createSocket() throws IOException {
        try {
            UUID uuid = UUID.fromString(MY_UUID);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 101);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return;
            }
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("createSocket", "Adapter");
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        mSocket.connect();
        OutputStream os = mSocket.getOutputStream();
        sender = new PrintStream(os);
        Log.d("createSocket", "Fertig, " + "Socket: " + mSocket + " Sender: " + sender + " OutputStream: " + os + " mDevice: " + mDevice.getName());
    }


    /**
     * a Simple method to change the text value of a text object.
     * @param text The text box to edit, TextView.
     * @param value The value we want to change the text to, String.
     */
    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }


    /**
     * The onCreate method is run when an instance of our app is created.
     * it sets variables for and initialises all UI elements
     * as well as connecting to the bluetooth device and initializing our
     * openCV Camera object.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializes UI objects
        Button button = findViewById(R.id.biasModifierButton);
        mSendBN = (Button) findViewById(R.id.testButton);
        //Sets an Event Listener for button to change bias value.
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bias == 1){
                    bias = 0;
                } else if(bias == 0){
                    bias = 1;
                }
                Log.d("biasModifier", "chagned bias to "+bias);
                Log.d("BUTTONS", "User tapped the biasModifierButton");
            }
        });
        //Initializes bluetooth adapter and connect to brick.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initBluetooth();
        findBrick();
        if (mDevice == null) {
            mSendBN.setClickable(false);
            Toast.makeText(this, "No Devices found or BT disabled", Toast.LENGTH_SHORT).show();
            Log.d("onC", "Connected to " + mDevice);
        }
        try {
            createSocket();
        } catch (IOException e) {
            Log.d("createSocket", "ERROR IN SOCKET MAKING");
            e.printStackTrace();
        }
        //Once connected initialize openCV Camera.
        getPermission();
        cameraBridgeViewBase = findViewById(R.id.camView);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            /**
             * This method is run as the Camera is initialized.
             * it initializes the variables the we will use to detect the lane
             * and calculate the bias.
             * @param width -  the width of the frames that will be delivered
             * @param height - the height of the frames that will be delivered
             */
            @Override
            public void onCameraViewStarted(int width, int height) {
                input = new Mat();
                roi = new Mat();
                mono = new Mat();
                blur = new Mat();
                thresh = new Mat();
                bias  = 0;
                //qrResult = findViewById(R.id.qrResult);
                angleResult = findViewById(R.id.angleResult);
            }

            /**
             * This method is run when the Camera is stopped
             * It is left blank because we do not use it in this program.
             */
            @Override
            public void onCameraViewStopped() {

            }

            /**
             * This method is run on every frame of the camera
             * it contains the bulk of our calculations and
             * detects the lane and figures out the distance between the camera and
             * the edge of the lane, The bias decides which edge we are using
             * @param inputFrame Each Frame the camera sees is passed to the method.
             * @return the Camera frame.
             */
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                input = inputFrame.rgba();

                //NOTE: These Lines filter our image to only the valid data we need
                Mat roi = new Mat(input, new Rect(10, 2*input.rows()/3, input.cols()-20, input.rows()/12));
                Imgproc.cvtColor(roi, mono, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(mono, blur, new Size(9, 9), 2, 2);
                Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);
                Mat erodeImg = new Mat();
                Mat erode = new Mat();
                Imgproc.erode(thresh, erodeImg, erode);
                Mat dilateImg = new Mat();
                Mat dilate = new Mat();
                Imgproc.dilate(erodeImg, dilateImg, dilate);
                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                Mat notused = new Mat();
                //NOTE: These lines found our distance from edge using the filtered images.
                Imgproc.findContours(dilateImg, contours, notused, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                double minMaxCx = (bias > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                for(MatOfPoint cont : contours) {
                    Moments mu = Imgproc.moments(cont, false);
                    if (mu.get_m00() > 100.0) {
                        Rect r = Imgproc.boundingRect(cont);
                        double cx;
                        if (bias > 0) {
                            cx = r.x + r.width - 12;
                            if (cx > minMaxCx) {
                                minMaxCx = cx;
                            }
                        } else {
                            cx = r.x + 12;
                            if (minMaxCx > cx) {
                                minMaxCx = cx;
                            }
                        }
                    }
                }
                if (Double.isInfinite(minMaxCx))
                    minMaxCx = roi.cols()/2;
                // return 1.0f - 2.0f*(float)minMaxCx/roi.cols();
                Log.d("ANGLE", String.valueOf(1.0f - 2.0f*(float)minMaxCx/roi.cols()));
                //angleResult.setText(String.valueOf(1.0f - 2.0f*(float)minMaxCx/roi.cols()));
                try {
                    onSend(String.valueOf(1.0f - 2.0f*(float)minMaxCx/roi.cols()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                setText(angleResult, String.valueOf(1.0f - 2.0f*(float)minMaxCx/roi.cols()));
                //sendMessage(String.valueOf(1.0f - 2.0f*(float)minMaxCx/roi.cols()));
                return input;
            }
        });

        if(OpenCVLoader.initDebug()) {
            Log.d("LOADED", "SUCCESS");
            cameraBridgeViewBase.enableView();
        } else {
            Log.d("LOADED", "ERROR");
        }
    }


    /**
     * This method is called when the app Resumes from a Paused state.
     * it will enable our camera.
     */
    @Override
    protected void onResume(){
        super.onResume();
        cameraBridgeViewBase.enableView();
    }


    /**
     * This method is used to send a message over our bluetooth socket.
     * @param message the message which we would like to send, String.
     * @throws IOException
     */
    public static void onSend(String message) throws IOException {
        try {
            OutputStream os = mSocket.getOutputStream();
            sender = new PrintStream(os);
            Log.d("onSend", "Message = " +  message);
            sender.println(message);
            sender.flush();
            Log.d("onSend", "Message sent");
            //mSocket.close();
            Log.d("onSend", "Socket closed");
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is called when our app is closed.
     * It is used to safely close our bluetooth connections without causing errors.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when our app is paused
     * it will disable to camera so that the app isn't draining
     * system resources whilst paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }


    /**
     * Overrides and OpenCV method to return a list of frames from our camera view.
     * @return singletonlist
     */
    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList(){
        return Collections.singletonList(cameraBridgeViewBase);
    }


    /**
     * A simple method to check if the phone has allowed the app to use the correct permissions.
     */
    void getPermission(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

    }


    /**
     * A simple method to request permission from the phone if not found
     * @param requestCode permission code, int.
     * @param permissions permissions to request, String[].
     * @param grantResult if permissions are granted, Int[].
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if(grantResult.length>0 && grantResult[0]!=PackageManager.PERMISSION_GRANTED){
            getPermission();
        }
    }
}