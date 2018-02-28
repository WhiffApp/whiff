package com.app.whiff.whiff.NonRootScanner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 *  This class is used for packet filter for displaying
 *
 * @author Yeo Pei Xuan
 */

public class PacketContentFilter implements Parcelable {
    public String protocol;
    public String sourceIP;
    public String destinationIP;
    public Long length;

    public static final Creator<PacketContentFilter> CREATOR = new Creator<PacketContentFilter>() {

        public PacketContentFilter createFromParcel(Parcel source) {
            PacketContentFilter filter = new PacketContentFilter();
            filter.protocol = source.readString();
            filter.sourceIP = source.readString();
            filter.destinationIP = source.readString();
            filter.length = source.readLong();

            return filter;
        }

        @Override
        public PacketContentFilter[] newArray(int size) {
            return new PacketContentFilter[size];
        }
    };

    public PacketContentFilter() {
        this.length = 0L;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(protocol);
        parcel.writeString(sourceIP);
        parcel.writeString(destinationIP);
        parcel.writeLong(length);
    }
}
