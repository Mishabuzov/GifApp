# Application for showing gif-memes about software developers' life.
The app is fully written in **Kotlin** and right now contains 1 screen with 2 separate tabs showing different categories of .gif-memes. Each tab contains 2 buttons for switching memes in chosen category.

### Technologies, libs, and features utilized:
1. **Retrofit2** + **Okhttp3** for sending and processing queries. **RxJava3** makes it asynchronically.
2. **Glide** for showing gif-files, with caching already loaded gif-images.
3. Showing ProgressBar while loading .gif-images. Separate network error processing on loading single .gif-file and whole data request. Ability to reload .gif or repeat request in case of network issues.
4. Handling device orientation changes.
5. Screen, contained ViewPager2 + TabLayout with separate **Fragments** displaying different categories of .gif-memes.

Clone the [repository](https://github.com/Mishabuzov/GifApp) and install *gifApp.apk* in order to launch the app.
