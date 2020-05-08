package sembako.sayunara.android.ui.component.account.address.mapaddress.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import sembako.sayunara.android.R


import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlacesAutoCompleteAdapter(private val mContext: Context) : RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder>(), Filterable {
    private var mResultList: ArrayList<PlaceAutocomplete>? = ArrayList()
    private val STYLEBOLD: CharacterStyle
    private val STYLENORMAL: CharacterStyle
    private val placesClient: PlacesClient
    private var clickListener: ClickListener? = null

    init {
        STYLEBOLD = StyleSpan(Typeface.BOLD)
        STYLENORMAL = StyleSpan(Typeface.NORMAL)
        placesClient = com.google.android.libraries.places.api.Places.createClient(mContext)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun click(place: Place, address : String)
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            @SuppressLint("LogNotTimber")
            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    Log.d("nodata"," ada data")
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    clear()
                    notifyDataSetChanged()
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                    Log.d("nodata","nodata")
                }
            }
        }
    }


    @SuppressLint("LogNotTimber")
    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutocomplete> {

        val resultList = ArrayList<PlaceAutocomplete>()

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        //https://gist.github.com/graydon/11198540
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                //.setCountry("BD")
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setCountry("ID")
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build()

        val autocompletePredictions = placesClient.findAutocompletePredictions(request)

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        return if (autocompletePredictions.isSuccessful) {
            val findAutocompletePredictionsResponse = autocompletePredictions.result
            if (findAutocompletePredictionsResponse != null)
                for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                    Log.i(TAG, prediction.placeId)
                    resultList.add(PlaceAutocomplete(prediction.placeId, prediction.getPrimaryText(STYLENORMAL).toString(), prediction.getFullText(STYLEBOLD).toString()))
                }

            resultList
        } else {
            resultList
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PredictionHolder {
        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = layoutInflater.inflate(R.layout.searchview_adapter, viewGroup, false)
        return PredictionHolder(convertView)
    }

    override fun onBindViewHolder(mPredictionHolder: PredictionHolder, i: Int) {
        mPredictionHolder.address.text = mResultList!![i].address
        mPredictionHolder.area.text = mResultList!![i].area
    }

    override fun getItemCount(): Int {
        return mResultList!!.size
    }

    fun clear() {
        mResultList?.clear()
    }

    fun getItem(position: Int): PlaceAutocomplete {
        return mResultList!![position]
    }

    inner class PredictionHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val address: TextView = itemView.findViewById(R.id.tv_detail_address)
        val area: TextView = itemView.findViewById(R.id.tv_address)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {

            if(!mResultList.isNullOrEmpty()){
                val item = mResultList!![adapterPosition]
                val placeId = item.placeId.toString()
                val address: String = mResultList!![adapterPosition].address.toString()

                val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient.fetchPlace(request).addOnSuccessListener { response ->
                    var place = response.place
                    clickListener!!.click(place,address)
                }.addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Toast.makeText(mContext, exception.message + "", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    inner class PlaceAutocomplete internal constructor(var placeId: CharSequence, var area: CharSequence, var address: CharSequence) {

        override fun toString(): String {
            return area.toString()
        }
    }

    companion object {
        private const val TAG = "PlacesAutoAdapter"
    }
}