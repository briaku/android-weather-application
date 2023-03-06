package edu.umb.cs443

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.umb.cs443.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.widget.AdapterView.OnItemSelectedListener

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    private val stringurlcity="https://api.openweathermap.org/data/2.5/weather?q=" // need to add city
    private val stringurlzip= "https://api.openweathermap.org/data/2.5/weather?zip=" // alternative zip code
    private val icondownloadurl= "https://openweathermap.org/img/wn/"
    private val apikey= ",us&APPID=e829494623208fdc55dd4d87cf5778d0"
    private lateinit var binding:ActivityMainBinding
    private var job: Job? = null// this is the job for downloading the temp data

    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val mFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mFragment.getMapAsync(this)
        //println(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this))
        binding.editText.inputType= InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        binding.editText.setSingleLine()
        val spinner= binding.spinner1

        var items = arrayOf("Save" , "weatherData1", "weatherData2")
        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,items )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter
        spinner.setBackgroundResource(R.drawable.quantum_ic_keyboard_arrow_down_white_36)


    }
    fun checknumeric(string: String):Boolean{
        return string.all { // return true if all the elements match true from the given predicate... isDigit
                character-> character.isDigit()
        }
    }

    fun checkConn(): Boolean {
        val connectionmanager=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? =connectionmanager.activeNetworkInfo
        return networkInfo?.isConnected==true
    }

    fun getWeatherInfo(v: View?) {
        if (checkConn()) {
           if (job==null|| !job!!.isActive) {// if we have never tried to retrieve info or the job to get the json is not currently running


               val userinput: String = binding.editText.getText().toString()
               Log.i(DEBUG_TAG, "userinput value " + userinput)
               Log.i(DEBUG_TAG, "true? " + checknumeric(userinput))
               establishConnection(userinput)
           }
            else {
                job!!.cancel()
               val userinput: String = binding.editText.getText().toString()
               Log.i(DEBUG_TAG, "userinput value " + userinput)
               Log.i(DEBUG_TAG, "true? " + checknumeric(userinput))
               establishConnection(userinput)


           }

        }
        else {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle("no network connection")
            //set message for alert dialog
            builder.setMessage("There is no network connection detected. Please ")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("OK"){dialogInterface, which ->
                Toast.makeText(applicationContext,"Please turn on a connection",Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }



    }



    override fun onMapReady(map: GoogleMap) {
        mMap = map
        val Boston= LatLng(42.3601, -71.0589)
        mMap!!.addMarker(
            MarkerOptions()
            .position(Boston)
            .title("Boston"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(Boston))
        mMap!!.setTrafficEnabled(true)
        mMap!!.uiSettings.setZoomControlsEnabled(true)
        mMap!!.uiSettings.setCompassEnabled(true)
    }


    companion object {
        const val DEBUG_TAG = "edu.umb.cs443.MYMSG"
    }





    fun establishConnection(city_or_zip:String){

        job= CoroutineScope(Dispatchers.IO).launch {
            var responseresult=""
            var responseresult2=""
            var inputs: InputStream? = null
            var inputs2:InputStream?=null
            if (checknumeric(city_or_zip)) { // if all the values are digits than it is a zip then call the correct http request, otherwise do the city http request
                try {
                    val urlstring =stringurlzip+city_or_zip+apikey
                    var URL= URL(urlstring)
                    var HttpURLConn=URL.openConnection() as HttpURLConnection
                    HttpURLConn.setReadTimeout(10000)// if the read exceeds timeout connection. Same idea with connect
                    HttpURLConn.setConnectTimeout(10000)
                    HttpURLConn.requestMethod= "GET" // we are getting data from the url
                    HttpURLConn.connect()
                    val response = HttpURLConn.responseCode
                    Log.i(MainActivity.DEBUG_TAG, "The response is: $response")
                    inputs= HttpURLConn.inputStream
                    val bufferReader=inputs.bufferedReader()

                    responseresult=
                        bufferReader.readLines().toString() // this byte of data will be converted to a string

                    Log.i(MainActivity.DEBUG_TAG, "this should return the data"+responseresult)
                    HttpURLConn.disconnect()

                    var JsonARRWHOLE=JSONArray(responseresult)
                    val jsonObj= JsonARRWHOLE.getJSONObject(0)
                    val mainjsonobjectparse=jsonObj.getJSONObject("main")
                    val temperature= mainjsonobjectparse.getDouble("temp")
                    var tempConversionK_F= Math.floor ((temperature-273.15)*(9/5) +32)
                    var coordweather=jsonObj.getJSONObject("coord")
                    val lat= coordweather.getDouble("lat")
                    val long=coordweather.getDouble("lon")
                    val jsonARRWeather=jsonObj.getJSONArray("weather")
                    val jsonObjWeather=jsonARRWeather.getJSONObject(0)
                    val iconimage= jsonObjWeather.getString("icon")
                    val locationname=jsonObj.getString("name") // gets the locationname
                    val description= jsonObjWeather.getString("description")
                    val feelslikeinfo= Math.floor( (mainjsonobjectparse.getDouble("feels_like")-273.15)*(9/5) +32)
                    val windInfoObject= jsonObj.getJSONObject("wind")
                    val windspeed= windInfoObject.getDouble("speed")


                    CoroutineScope(Dispatchers.IO).launch {

                       try {
                           val urlstring2 = icondownloadurl +iconimage +"@2x.png"
                           Log.i(MainActivity.DEBUG_TAG, "this is the iconurl" + urlstring2)
                           var URL2 = URL(urlstring2)
                           var HttpURLConn2 = URL2.openConnection() as HttpURLConnection
                           HttpURLConn2.setReadTimeout(10000)// if the read exceeds timeout connection. Same idea with connect
                           HttpURLConn2.setConnectTimeout(10000)
                           HttpURLConn2.requestMethod = "GET" // we are getting data from the url
                           HttpURLConn2.connect()
                           val response2 = HttpURLConn2.responseCode
                           Log.i(MainActivity.DEBUG_TAG, "The response of response2 is: $response2")
                           inputs2 = HttpURLConn2.inputStream
                           var bitmap= BitmapFactory.decodeStream(inputs2)
                           withContext(Dispatchers.Main){
                               binding.imageView.setImageBitmap(bitmap)
                               val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadeout)
                               binding.textView.startAnimation(animation)
                               binding.textView.setText(tempConversionK_F.toString()+"°F")
                               val animation2 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadein)
                               binding.textView.startAnimation(animation2)

                               binding.feelslikeinfo.startAnimation(animation)
                               binding.feelslikeinfo.setText("feels like:  "+feelslikeinfo.toString())
                               binding.feelslikeinfo.startAnimation(animation2)

                               binding.weatherdescription.startAnimation(animation)
                               binding.weatherdescription.setText(description)
                               binding.weatherdescription.startAnimation(animation2)

                               binding.windspeed.startAnimation(animation)
                               binding.windspeed.setText("Windspeed:  "+windspeed.toString())
                               binding.windspeed.startAnimation(animation2)



                               var ziploc=LatLng(lat, long)
                               val center = CameraUpdateFactory.newLatLng(ziploc)
                               val zoom = CameraUpdateFactory.zoomTo(10f)
                               mMap!!.clear()
                               mMap!!.moveCamera(center)
                               mMap!!.animateCamera(zoom, 2000, null)
                               mMap!!.addMarker(
                                   MarkerOptions()
                                       .position(ziploc)
                                       .title("New POS")) // need to get the name jsonobject


                           }
                           Log.i(MainActivity.DEBUG_TAG, "this is the bitmap" + bitmap)
                           Log.i(MainActivity.DEBUG_TAG, "this should return the data from the iconurl" + responseresult2)
                           //HttpURLConn2.disconnect()
                       }
                       catch (e:Exception){
                           Log.i(MainActivity.DEBUG_TAG, "Failed for somereason")
                       }
                    }








                    //val mainJsonObject= JsonParser.getJSONObject("main")
                    //Log.i(DEBUG_TAG,"THis is the whole json object from the api that is a array"+ JsonARRWHOLE.toString())
                   // Log.i(DEBUG_TAG,"THis is the actual json object "+ jsonObj.toString())
                    //Log.i(DEBUG_TAG,"THis is the main jsonobject"+ mainjsonobjectparse.toString())
                    //Log.i(DEBUG_TAG,"this is the temperature"+ temperature.toString())

                }catch (e:Exception){
                    Log.i(MainActivity.DEBUG_TAG, "Failed to establish connection"+e.toString()) // print out the exception
                    withContext(Dispatchers.Main) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        //set title for alert dialog
                        builder.setTitle("Invaild City of Zip")
                        //set message for alert dialog
                        builder.setMessage("Please enter a valid City or Zip")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        //performing positive action
                        builder.setPositiveButton("OK"){dialogInterface, which ->
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }

                }




            }


            else {
                try{
                    val urlstring= stringurlcity+city_or_zip+apikey
                    var URL= URL(urlstring)
                    val HttpURLConn=URL.openConnection() as HttpURLConnection
                    HttpURLConn.setReadTimeout(10000)// if the read exceeds timeout connection. Same idea with connect
                    HttpURLConn.setConnectTimeout(10000)
                    HttpURLConn.requestMethod= "GET" // we are getting data from the url
                    HttpURLConn.connect()
                    val response = HttpURLConn.responseCode
                    Log.i(DEBUG_TAG, "The response is: $response")
                    inputs= HttpURLConn.inputStream
                    val bufferReader=inputs.bufferedReader()

                    responseresult=bufferReader.readLines().toString() // this byte of data will be converted to a string
                    Log.i(DEBUG_TAG, "this should return the data"+responseresult)
                    HttpURLConn.disconnect()




                    var JsonARRWHOLE=JSONArray(responseresult)
                    val jsonObj= JsonARRWHOLE.getJSONObject(0)
                    val mainjsonobjectparse=jsonObj.getJSONObject("main")
                    val temperature= mainjsonobjectparse.getDouble("temp")
                    var tempConversionK_F= Math.floor ((temperature-273.15)*(9/5) +32)
                    var coordweather=jsonObj.getJSONObject("coord")
                    val lat= coordweather.getDouble("lat")
                    val long=coordweather.getDouble("lon")
                    val jsonARRWeather=jsonObj.getJSONArray("weather")
                    val jsonObjWeather=jsonARRWeather.getJSONObject(0)
                    val iconimage= jsonObjWeather.getString("icon")
                    val locationname=jsonObj.getString("name") // gets the locationname
                    val description= jsonObjWeather.getString("description")
                    val feelslikeinfo= Math.floor( (mainjsonobjectparse.getDouble("feels_like")-273.15)*(9/5) +32)
                    val windInfoObject= jsonObj.getJSONObject("wind")
                    val windspeed= windInfoObject.getDouble("speed")
                    CoroutineScope(Dispatchers.IO).launch {

                        try {
                            val urlstring2 = icondownloadurl +iconimage +"@2x.png"
                            Log.i(MainActivity.DEBUG_TAG, "this is the iconurl" + urlstring2)
                            var URL2 = URL(urlstring2)
                            var HttpURLConn2 = URL2.openConnection() as HttpURLConnection
                            HttpURLConn2.setReadTimeout(10000)// if the read exceeds timeout connection. Same idea with connect
                            HttpURLConn2.setConnectTimeout(10000)
                            HttpURLConn2.requestMethod = "GET" // we are getting data from the url
                            HttpURLConn2.connect()
                            val response2 = HttpURLConn2.responseCode
                            Log.i(MainActivity.DEBUG_TAG, "The response of response2 is: $response2")
                            inputs2 = HttpURLConn2.inputStream
                            var bitmap= BitmapFactory.decodeStream(inputs2)
                            withContext(Dispatchers.Main){
                                binding.imageView.setImageBitmap(bitmap)
                                val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadeout)
                                binding.textView.startAnimation(animation)
                                binding.textView.setText(tempConversionK_F.toString()+"°F")
                                val animation2 = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fadein)
                                binding.textView.startAnimation(animation2)


                                binding.feelslikeinfo.startAnimation(animation)
                                binding.feelslikeinfo.setText("feels like:  "+feelslikeinfo.toString())
                                binding.feelslikeinfo.startAnimation(animation2)

                                binding.weatherdescription.startAnimation(animation)
                                binding.weatherdescription.setText(description)
                                binding.weatherdescription.startAnimation(animation2)

                                binding.windspeed.startAnimation(animation)
                                binding.windspeed.setText("Windspeed:  "+windspeed.toString())
                                binding.windspeed.startAnimation(animation2)


                                var ziploc=LatLng(lat, long)
                                val center = CameraUpdateFactory.newLatLng(ziploc)
                                val zoom = CameraUpdateFactory.zoomTo(10f)
                                mMap!!.clear()
                                mMap!!.moveCamera(center)
                                mMap!!.animateCamera(zoom, 2000, null)
                                mMap!!.addMarker(
                                    MarkerOptions()
                                        .position(ziploc)
                                        .title("New POS")) // need to get the name jsonobject

                            }
                            Log.i(MainActivity.DEBUG_TAG, "this is the bitmap" + bitmap)
                            Log.i(MainActivity.DEBUG_TAG, "this should return the data from the iconurl" + responseresult2)
                            HttpURLConn2.disconnect()
                        }
                        catch (e:Exception){
                            Log.i(MainActivity.DEBUG_TAG, "Failed for somereason")
                        }
                    }
                }catch (e:Exception){
                    Log.i(DEBUG_TAG, "Failed to establish connection"+e.toString()) // print out the exception
                    withContext(Dispatchers.Main) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        //set title for alert dialog
                        builder.setTitle("Invaild City of Zip")
                        //set message for alert dialog
                        builder.setMessage("Please enter a valid City or Zip")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                        //performing positive action
                        builder.setPositiveButton("OK"){dialogInterface, which ->
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }



                }





            }






        }






}}

