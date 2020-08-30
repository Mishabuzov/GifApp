package ru.home.gifapp.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import ru.home.gifapp.GifNetworkWrapper

interface GifService {

    @GET("{category}/{page}")
    fun fetchGifsByCategory(
        @Path("category") category: String,
        @Path("page") page: Int
    ): Single<GifNetworkWrapper>

}
