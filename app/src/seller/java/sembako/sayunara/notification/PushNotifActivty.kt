package sembako.sayunara.notification

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.seller.activity_form_notification.*
import org.json.JSONException
import org.json.JSONObject
import sembako.sayunara.android.R
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.fcm.MySingleton
import java.util.HashMap

internal class PushNotifActivty : AppCompatActivity() {

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAAn_CPUVE:APA91bEPh_fqJpL43QCZS0w7B44f5K3b8UIvAFuHRthkX1lliTTWhQQneUJ6lgkmedPos04jCYHsnVV2VLfaCFmwEUYOzKnsnTu6u27jyRM3T9nEtV34Od7Nv_kmRMcTuCqLPx6JoxXC"
    private val contentType = "application/json"
    val TAG = "NOTIFICATION TAG"
    var NOTIFICATION_TITLE: String? = null
    var NOTIFICATION_MESSAGE: String? = null
    var TOPIC: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_form_notification)
        /*edtTitle = findViewById(R.id.edtTitle)
        edtMessage = findViewById(R.id.edtMessage)*/
        val btnSend = findViewById<Button>(R.id.btnSend)
        btnSend.setOnClickListener {
            TOPIC = Constant.Topic.topicGeneral //topic has to match what the receiver subscribed to
            NOTIFICATION_TITLE = edtTitle.text.toString()
            NOTIFICATION_MESSAGE = edtMessage.text.toString()
            val notification = JSONObject()
            val notifcationBody = JSONObject()
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE)
                notifcationBody.put("message", NOTIFICATION_MESSAGE)
                notification.put("to", TOPIC)
                notification.put("data", notifcationBody)
            } catch (e: JSONException) {
                Log.e(TAG, "onCreate: " + e.message)
            }
            sendNotification(notification)
        }
    }

    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener { response ->
                Log.i(TAG, "onResponse: $response")
                edtTitle!!.setText("")
                edtMessage!!.setText("")
            },
            Response.ErrorListener {
                Toast.makeText(this@PushNotifActivty, "Request error", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onErrorResponse: Didn't work")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
    }
}