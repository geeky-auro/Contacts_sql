package com.aurosaswatraj.contentprovider

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.aurosaswatraj.contentprovider.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    //We dont need anymore readgranted field ......
//    private var readGranted = false

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        val hasreadContactsPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, "CheckSelfPermission Returned $hasreadContactsPermission")

        if (hasreadContactsPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate :permission Granted")
//            readGranted = true
        } else {
            Log.d(TAG, "onCreate Requesting Permission")
            ActivityCompat.requestPermissions(
                this, arrayOf(READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
        }
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            Log.d(TAG, ".FAB onClick Starts")
//            if (readGranted)
            if(ContextCompat.checkSelfPermission(this, READ_CONTACTS)==PackageManager.PERMISSION_GRANTED)
            {
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, projection, null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()//// create a list to hold our contacts
                Snackbar.make(it, "Permission Enabled", Snackbar.LENGTH_INDEFINITE).setAction("OK",
                    {
                        Toast.makeText(it.context,"Permission Enabled",Toast.LENGTH_SHORT).show()
                    })
                    .show()
                cursor?.use {//// loop through the cursor
                    while (it.moveToNext()) {
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }
                //Create adapter for listView..
                val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
                contacts_names.adapter = adapter
            }
            else{
                Snackbar.make(it, "Please grant access to your contacts", Snackbar.LENGTH_INDEFINITE).setAction("Grant Access",{
                 if(ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS))
                {
                    Log.d(TAG,"Snackbar onCLick calling request permissions")
                    ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS),
                        REQUEST_CODE_READ_CONTACTS)
                }
                else
                {
                    //User has permanently denied the permission take them to the settings.
                    Log.d(TAG,"onClick launching settings")
                    val intent=Intent()
                    intent.action=Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    var uri=Uri.fromParts("package",this.packageName,null)
                    Log.d(TAG,"Snackbar onClick Uri is $uri")
                    intent.data=uri
                    this.startActivity(intent)
                    }
                })
                    .show()
            }

           Log.d(TAG, ".FAB onClick Ends")
        }
        Log.d(TAG, ".onCraete ENds")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionResults starts")
        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {

                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //Permission was granted !! YAY
                    {// Contacts Related tasks we need to do..!!

                        Log.d(TAG, "onRequestPermissionResult:permission Granted./")

                    } else {//Permission Denied
                        //Functionality depends upon the permission
                        Log.d(TAG, "onRequestPermissionResult:permission Refused./")

                    }
                //To disable a fab button if permission is not granted!!
//              binding.fab.isEnabled=readGranted

            }
        }
        Log.d(TAG, "onResultPermissionResult Ends..")


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}


//TO remove saving of state