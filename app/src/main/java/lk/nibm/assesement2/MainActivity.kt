package lk.nibm.assesement2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

data class DogImageResponse(
    val url: String,
)

data class CatImageResponse(
    val url: String,
)

class MainActivity : AppCompatActivity() {

    lateinit var spn_pet:Spinner
    lateinit var img_pet:ImageView
    lateinit var btn_click:Button

    private var currentCatImageUrl: String? = null
    private var currentDogImageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val petTypes = arrayOf("Select Pet","Cat","Dog")

        spn_pet = findViewById(R.id.spn_pet)
        img_pet = findViewById(R.id.img_pet)
        btn_click = findViewById(R.id.btn_click)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_pet.adapter = adapter

        spn_pet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPetType = spn_pet.selectedItem.toString()
                when (selectedPetType) {
                    "Cat" -> fetchCatImage()
                    "Dog" -> fetchDogImage()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        btn_click.setOnClickListener {
            val selectedPetType = spn_pet.selectedItem.toString()
            if (selectedPetType != "Select Pet"){
                when (selectedPetType) {
                    "Cat" -> fetchCatImage()
                    "Dog" -> fetchDogImage()
                }
            }
        }
    }

    private fun fetchCatImage() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.thecatapi.com/v1/images/search")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                response.body()?.use {
                    if (response.isSuccessful) {
                        val responseBody = it.string()
                        val catImageResponse = Gson().fromJson(responseBody, Array<CatImageResponse>::class.java)
                        val imageUrl = catImageResponse[0].url
                        currentCatImageUrl = imageUrl
                        loadImage(imageUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchDogImage() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://random.dog/woof.json")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                response.body()?.use {
                    if (response.isSuccessful) {
                        val responseBody = it.string()
                        val dogImageResponse = Gson().fromJson(responseBody, DogImageResponse::class.java)
                        val imageUrl = dogImageResponse.url
                        currentDogImageUrl = imageUrl
                        loadImage(imageUrl)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadImage(imageUrl: String?) {
        runOnUiThread {
            if (!imageUrl.isNullOrBlank()) {
                Picasso.get().load(imageUrl).into(img_pet)
            }
        }
    }
}



