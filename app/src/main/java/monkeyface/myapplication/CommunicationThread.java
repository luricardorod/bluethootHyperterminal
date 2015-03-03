package monkeyface.myapplication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommunicationThread extends Thread {
    private final BluetoothSocket mmSocket;

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final DataInputStream mDataStream;

    private MessageBluetoothHandler handler;

    public CommunicationThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mDataStream = new DataInputStream(tmpIn);
    }

    public void setHandler(MessageBluetoothHandler handler) {
        this.handler = handler;
    }

    public void run() {
        byte[] buffer = new byte[1];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                mDataStream.readFully(buffer, 0, 1);
                // Send the obtained bytes to the UI activity

                Log.i("Response", new String(buffer,"UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static interface MessageBluetoothHandler {
        public void onMessageReceived(byte[] msg);
    }
}
