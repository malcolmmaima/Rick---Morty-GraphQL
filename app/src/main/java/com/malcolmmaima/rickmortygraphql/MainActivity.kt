package com.malcolmmaima.rickmortygraphql

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {

    private val characters = mutableStateOf(listOf<CharacterModel>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apolloClient = ApolloClient.builder()
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
            Column {
                characters.value.forEach { character ->
                    character.name?.let { Text(text = it, modifier = Modifier.padding(16.dp)) }
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

data class CharacterModel(val id: String?, val name: String?)
