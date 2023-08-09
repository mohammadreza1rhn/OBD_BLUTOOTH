package ir.device.obd

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import java.io.IOException
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var context: Context



    private val PERMISSION_REQUEST_BLU = 102

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var receiver:BluetoothReceiver
    lateinit var receiver2:Discoverability
    private lateinit var btOnOff:Button
    private lateinit var btDiscoverability:Button
    private lateinit var btGetPairedDevices:Button
    private lateinit var btDiscoverDevices:Button

    private lateinit var device1:TextView
    private lateinit var device2:TextView
    private lateinit var device3:TextView

    private lateinit var foundedDevice:BluetoothDevice

    val REQUEST_ACCESS_COARSE_LOCATION=101

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btOnOff=findViewById(R.id.btOnOff)
        btDiscoverability=findViewById(R.id.btDiscoverability)
        btGetPairedDevices=findViewById(R.id.btGetPairedDevices)
        btDiscoverDevices=findViewById(R.id.btDiscoverDevices)

        device1=findViewById(R.id.device1)
        device2=findViewById(R.id.device2)
        device3=findViewById(R.id.device3)



        requestPermission()

        bluetoothManager=getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter=bluetoothManager.adapter
        enableDisableBT()
        enableDisableDiscoverability()
        receiver=BluetoothReceiver()
        receiver2= Discoverability()
//        btOnOff.setOnClickListener{
//
//        }
        btGetPairedDevices.setOnClickListener{
            getPairedDevices()
        }

        btDiscoverDevices.setOnClickListener{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                when(ContextCompat.checkSelfPermission(baseContext,Manifest.permission.ACCESS_COARSE_LOCATION)){
                    PackageManager.PERMISSION_DENIED->androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("RunTime vPermision")
                        .setMessage("Give Permision")
                        .setNeutralButton("okay",DialogInterface.OnClickListener{dialog,which->
                            if(ContextCompat.checkSelfPermission(baseContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_ACCESS_COARSE_LOCATION)
                            }
                        }).show()
//                        .findViewById<TextView>(R.id.message)!!.movementMethod=LinkMovementMethod.getInstance()


                    PackageManager.PERMISSION_GRANTED->{
                        Log.d("message1","permission granted")
                    }
                }
            }
            discoverDevices()
        }


    }





    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }


    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        Log.e("message1","discover device")
       val filter=IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        registerReceiver(discoverDeviceReceiver,filter)
        bluetoothAdapter.startDiscovery()
        Log.e("message1",bluetoothAdapter.startDiscovery().toString())

    }

    var it=0
    private val discoverDeviceReceiver=object :BroadcastReceiver(){

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {

            var action=""
            if(intent!=null){
                 action= intent.action.toString()
            }
            when(action){
                BluetoothAdapter.ACTION_STATE_CHANGED->{
                    Log.d("message1","States Changed")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED->{
                    Log.d("message1","discovery start ")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED->{
                    Log.d("message1","discovery finished ")

                }
                BluetoothDevice.ACTION_FOUND ->{
                    val device:BluetoothDevice =
                        intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if(device!=null){
                        Log.d("message1","${device.name} ${device.address} ${device.uuids}")
                        it++
//                        when(it){
//                            1->{
//                                device1.text=device.address
//                                device1.setOnClickListener{
//                                    device.createBond()
//                                }
//                            }
//                            2->{
//                                device2.text=device.address
//                                device2.setOnClickListener{
//                                    device.createBond()
//                                }
//                            }
//                            3->{
//                                device3.text=device.address
//                                device3.setOnClickListener{
//                                    device.createBond()
//                                }
//                            }
//                        }
                        if(!device.name.isNullOrEmpty()){
                            device1.text=device.name
                            foundedDevice=device
//                            device.createRfcommSocketToServiceRecord(UUID.fromString(device.uuids.toString()))

                            if(device.name=="YSW10"){
//                                AcceptThread().run()
//                                ConnectThread(device).run()
                            }
                        }
                        when(device.bondState){
                            BluetoothDevice.BOND_NONE->{
                                Log.d("message1","${device.name} bond none")
                            }
                            BluetoothDevice.BOND_BONDING->{
                                Log.d("message1","${device.name} bond BONDING")
                            }
                            BluetoothDevice.BOND_BONDED->{
                                Log.d("message1","${device.name} bond BONDED")
                            }
                        }

                    }
                }
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun getPairedDevices(){
       var arr= bluetoothAdapter.bondedDevices
        Log.d("message1",arr.size.toString())
        Log.d("message1",arr.toString())
        for(device in arr){
            Log.d("message1",device.name+ " " +device.address +" " + device.uuids)
        }
    }

    private fun enableDisableDiscoverability() {
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE)==PackageManager.PERMISSION_GRANTED->{

            }
            shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_ADVERTISE)->{
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE),101)
            }
        }
        btDiscoverability.setOnClickListener{
            val discoverIntent=Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,20)
            startActivity(discoverIntent)

            val intentFilter=IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            registerReceiver(receiver2,intentFilter)
        }
    }

    private fun enableDisableBT() {
        Log.d("message1","enableBt")
        when{
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_GRANTED->{

            }
            shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)->{
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.BLUETOOTH_CONNECT),101)
            }
        }
        btOnOff.setOnClickListener{
            if(!bluetoothAdapter.isEnabled){
                bluetoothAdapter.enable()
                val intent =Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivities(arrayOf(intent))

                val intentFilter=IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver,intentFilter)
            }
            if(bluetoothAdapter.isEnabled){
                bluetoothAdapter.disable()

                val intentFilter=IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver,intentFilter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(receiver2)
    }



    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("OBD", UUID.fromString("6680b2c1-bd6f-41b0-b3be-023ca4cb4fd3"))
        }

        override fun run() {
// Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.e("message1", "running")
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("message1", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
//                    manageMyConnectedSocket(it)
                    Log.e("message1", "manage cc1")
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("message1", "Could not close the connect socket", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString("6680b2c1-bd6f-41b0-b3be-023ca4cb4fd3"))
        }

        public override fun run() {
// Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
// Connect to the remote device through the socket. This call blocks
// until it succeeds or throws an exception.
                socket.connect()

// The connection attempt succeeded. Perform work associated with
// the connection in a separate thread.
//                manageMyConnectedSocket(socket)
                Log.e("message1", "manage cc2")
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("messag1", "Could not close the client socket", e)
            }
        }
    }


}