In order to run this app please substitute your key in build.grade of modulde:app. i.e.replace api_key string value with your generated key as:
This is Stage 1 of Popular Movies App.

Stage 1:  Main Discovery Screen, A Details View, and Settings

In this stage we build the core experience of my movies app.

What this app does?
Upon launch, it presents the user with an grid arrangement of movie posters.
Allows user to change sort order via a setting:
The sort order can be by most popular, or by top rated
Allows the user to tap on a movie poster and transition to a details screen with additional information such as:
original title
movie poster image thumbnail
A plot synopsis (called overview in the api)
user rating (called vote_average in the api)
release date

To fetch popular movies, we will use the API from themoviedb.org.
If you don’t already have an account, you will need to create one in order to request an API Key.
In order to run this app please substitute your key in build.grade of modulde:app. 
i.e.replace api_key string value with your generated key as:
buildTypes.each {
        it.buildConfigField 'String', 'API_KEY', "\"api_key\""
    }
