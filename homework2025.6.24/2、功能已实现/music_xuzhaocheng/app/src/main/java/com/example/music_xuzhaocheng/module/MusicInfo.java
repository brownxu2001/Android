// MusicInfo.java
package com.example.music_xuzhaocheng.module;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Parcelable {
    private long id; // 音乐ID
    private String musicName; // 音乐名称
    private String author; // 作者/歌手
    private String coverUrl; // 封面图
    private String musicUrl;
    private String lyricUrl; // 音乐歌词文件地址
    private boolean isLiked;
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    // 添加 CREATOR 静态字段
    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            return new MusicInfo(in);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    // getter方法
    public long getId() { return id; }
    public String getMusicName() { return musicName; }
    public String getAuthor() { return author; }
    public String getCoverUrl() { return coverUrl; }
    public String getMusicUrl() { return musicUrl; }
    public String getLyricUrl() { return lyricUrl; }

    // 添加setter方法
    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    // 添加 writeToParcel 方法
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeLong(id);
        dest.writeString(musicName);
        dest.writeString(author);
        dest.writeString(coverUrl);
        dest.writeString(musicUrl);
        dest.writeString(lyricUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 添加构造函数用于从 Parcel 创建对象
    private MusicInfo(Parcel in) {
        isLiked = in.readByte() != 0;
        id = in.readLong();
        musicName = in.readString();
        author = in.readString();
        coverUrl = in.readString();
        musicUrl = in.readString();
        lyricUrl = in.readString();
    }
}