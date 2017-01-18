package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prernamanaktala on 11/15/16.
 */

public class Movie implements Parcelable{
    private String title;
    private String poster;
    private String releaseDate;
    private String synopsis;
    private String voteAverage;
    private int id;


    private Movie(Parcel in)
    {
        title= in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        synopsis = in.readString();
        voteAverage = in.readString();
        id = in.readInt();
    }

    public Movie() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = "http://image.tmdb.org/t/p/w185/"+poster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final static Parcelable.Creator CREATOR = new  Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        dest.writeString(voteAverage);
        dest.writeInt(id);
    }

}
