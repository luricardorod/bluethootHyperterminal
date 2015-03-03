package monkeyface.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class TerminalActivity extends ActionBarActivity implements ConnectThread.BluetoothThreadCallback,
        CommunicationThread.MessageBluetoothHandler {

    private EditText mMsgInput;
    private BluetoothSocket mSocket;
    private CommunicationThread communicationThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        mMsgInput = (EditText) findViewById(R.id.text_msg_input);
        Button mSendButton = (Button) findViewById(R.id.btn_send_msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Recover the device
        BluetoothDevice deviceReceived = getIntent()
                .getParcelableExtra(MainActivity.BLUETOOTH_DEVICE_KEY);
        connectDevice(deviceReceived);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terminal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectDevice(BluetoothDevice device) {
        ConnectThread bluetoothThreadConnection = new ConnectThread(device);
        bluetoothThreadConnection.setCallback(this);
        bluetoothThreadConnection.start();
    }

    @Override
    public void connectionEstablished(BluetoothSocket socket) {
        Log.i(TerminalActivity.class.getSimpleName(), "Socket state: " + socket.isConnected());
        communicationThread = new CommunicationThread(socket);
        communicationThread.setHandler(this);
        communicationThread.start();
    }

    public void sendMessage(View view) {
        if(mMsgInput.length() != 0) {
            String msgToSend = mMsgInput.getText().toString();
            byte[] msgInBytes = msgToSend.getBytes();

            communicationThread.write(msgInBytes);
        }
    }


    @Override
    public void onMessageReceived(byte[] msg) {
        Log.i(TerminalActivity.class.getSimpleName(), "Socket state: " + msg);
    }
}
