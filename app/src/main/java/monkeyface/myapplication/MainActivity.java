package monkeyface.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;


public class MainActivity extends ActionBarActivity {

    public static final String BLUETOOTH_DEVICE_KEY = "bluetooth_device";
    int REQUEST_ENABLE_BT = 1;

    private ListView listDevices;
    private BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
             //Activate bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        listDevices = (ListView) findViewById(R.id.list_devices);
        Button btnSync = (Button) findViewById(R.id.btn_bluetooth);

        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pairedDevices != null && !pairedDevices.isEmpty()) {
                    BluetoothDevice[] devices = new BluetoothDevice[pairedDevices.size()];
                    pairedDevices.toArray(devices);

                    initMsgActivity(devices[position]);
                }
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDevices.setAdapter(getBoundedDevices());
            }
        });

    }

    private void initMsgActivity(BluetoothDevice device) {
        Intent terminalIntent = new Intent(this, TerminalActivity.class);
        terminalIntent.putExtra(BLUETOOTH_DEVICE_KEY, device);
        startActivity(terminalIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public ArrayAdapter getBoundedDevices() {

        //TODO: Cambiar adaptador
        ArrayAdapter<String> adapterDevicesList = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices

            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                adapterDevicesList.add(device.getName() + "\n" + device.getAddress());
            }
        }

        //Cancel because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        return adapterDevicesList;
    }

}
