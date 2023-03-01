package edu.umb.cs443
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class justatest {
    private var job2: Job?=null
    private val stringurlcity="https://api.openweathermap.org/data/2.5/weather?q=" // need to add city
    private val stringurlzip= "https://api.openweathermap.org/data/2.5/weather?zip="
    private val apikey= ",us&APPID=e829494623208fdc55dd4d87cf5778d0"
    private lateinit var binding:ActivityMainBinding
    fun checknumeric(string: String):Boolean{
        return string.all { // return true if all the elements match true from the given predicate... isDigit
                character-> character.isDigit()
        }
    }




    fun establishConnection2(city_or_zip:String){
    var responseresult=""
    job2= CoroutineScope(Dispatchers.IO).launch {
        var inputs: InputStream? = null
        if (checknumeric(city_or_zip)) { // if all the values are digits than it is a zip then call the correct http request, otherwise do the city http request
            try {
                val urlstring =stringurlzip+city_or_zip+apikey
                var URL= URL(urlstring)
                val HttpURLConn=URL.openConnection() as HttpURLConnection
                HttpURLConn.setReadTimeout(10000)// if the read exceeds timeout connection. Same idea with connect
                HttpURLConn.setConnectTimeout(10000)
                HttpURLConn.requestMethod= "GET" // we are getting data from the url
                HttpURLConn.connect()
                val response = HttpURLConn.responseCode
                Log.i(MainActivity.DEBUG_TAG, "The response is: $response")
                if (response!=200){
                    job2?.cancel()
                }
                inputs= HttpURLConn.inputStream
                val bufferReader=inputs.bufferedReader()

                responseresult=
                    bufferReader.readLines().toString() // this byte of data will be converted to a string
                Log.i(MainActivity.DEBUG_TAG, "this should return the data"+responseresult)
                var JsonARRWHOLE= JSONArray(responseresult)
                val jsonObj= JsonARRWHOLE.getJSONObject(0)
                val jsonARRWeather=jsonObj.getJSONArray("weather")
                val jsonObjWeather=jsonARRWeather.getJSONObject(0)
                val iconimage= jsonObjWeather.getString("icon")



                HttpURLConn.disconnect()

            }catch (e:Exception){
                Log.i(MainActivity.DEBUG_TAG, "Failed to establish connection"+e.toString()) // print out the exception

            }



        }


    }














    }





}