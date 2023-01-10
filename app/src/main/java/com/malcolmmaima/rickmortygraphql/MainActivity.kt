package com.malcolmmaima.rickmortygraphql

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.malcolmmaima.rickmortygraphql.models.CharacterModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {

    private val characters = mutableStateOf(listOf<CharacterModel>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apolloClient = ApolloClient.Builder()
            .serverUrl("https://rickandmortyapi.com/graphql/")
            .okHttpClient(OkHttpClient.Builder().build())
            .build()

        // Fetch characters data using Apollo
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(AllCharactersQuery()).execute()
            }
            handleResponse(response)
        }

        setContent {
            CharactersComposable()
        }
    }

    @Composable
    private fun CharactersComposable() {
        MaterialTheme {
            ScrollableColumn()
        }
    }

    @Composable
    private fun ScrollableColumn(){
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(characters.value.size) { index ->
                val character = characters.value[index]
                character.imageUrl?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = character.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
                character.let {
                    character.name?.let { name -> Text(text = name, modifier = Modifier.padding(16.dp)) }
                    Image(painter = rememberImagePainter(character.imageUrl), contentDescription = character.name, modifier = Modifier
                        .fillMaxWidth().height(200.dp).padding(4.dp))
                }

            }
        }
    }


    private fun handleResponse(response: ApolloResponse<AllCharactersQuery.Data>) {
        if (response.hasErrors()) {
            // Handle errors
        } else {
            val characters = response.data?.characters?.results?.map {
                CharacterModel(it?.image, it?.name)
            }
            this.characters.value = characters ?: emptyList()
        }
    }

}
