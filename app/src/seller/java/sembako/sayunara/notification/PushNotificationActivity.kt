package sembako.sayunara.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import sembako.sayunara.android.constant.Constant
import sembako.sayunara.android.databinding.ActivityFormNotificationBinding
import sembako.sayunara.android.databinding.LayoutProgressBarWithTextBinding
import sembako.sayunara.android.databinding.ToolbarBinding
import sembako.sayunara.android.fcm.MySingleton
import sembako.sayunara.android.ui.base.BaseActivity
import sembako.sayunara.android.ui.component.mobile.base.setToolbar
import java.util.HashMap

internal class PushNotificationActivity : BaseActivity() {

    val TAG = "NOTIFICATION TAG"
    var title: String? = null
    var message: String? = null
    var topic: String? = null

    private lateinit var binding : ActivityFormNotificationBinding
    private lateinit var layoutProgressBinding: LayoutProgressBarWithTextBinding
    private lateinit var toolbarBinding : ToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormNotificationBinding.inflate(layoutInflater)
        layoutProgressBinding = LayoutProgressBarWithTextBinding.inflate(layoutInflater)
        toolbarBinding = ToolbarBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setToolbar(toolbarBinding.toolbar)

        binding.btnSend.setOnClickListener {
            topic = Constant.Topic.topicGeneral //topic has to match what the receiver subscribed to
            title = binding.edtTitle.text.toString()
            message = binding.edtMessage.text.toString()

            when {
                title=="" -> {
                    setToast("Isi Judul notifikasi")
                }
                message.toString().length>30 -> {
                    setToast("Konten notifikasi terlalu panjang max 30 char")
                }
                message.toString()=="" -> {
                    setToast("isi kontent notifikasi")
                }
                else -> {
                    layoutProgressBinding.layoutProgress.visibility = View.VISIBLE
                    val notification = JSONObject()
                    val notificationBody = JSONObject()
                    try {
                        notificationBody.put("title", title)
                        notificationBody.put("message", message)
                        notificationBody.put("type","main")
                        notification.put("to", topic)
                        notification.put("data", notificationBody)
                    } catch (e: JSONException) {
                        Log.e(TAG, "onCreate: " + e.message)
                    }
                    sendNotification2(notification)
                }
            }

        }
    }


    private fun sendNotification2(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener { response ->
                Log.i(TAG, "onResponse: $response")
                layoutProgressBinding.layoutProgress.visibility = View.GONE
                setToast("Berhasil mengirim Notifikasi")
            },
            Response.ErrorListener {
                Toast.makeText(activity, "Request error", Toast.LENGTH_LONG).show()
                Log.i(TAG, "onErrorResponse: Didn't work")
                layoutProgressBinding.layoutProgress.visibility = View.GONE
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = Constant.Key.ServerKeyFirebase
                params["Content-Type"] = "application/json"
                return params
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
    }
}