package osp.leobert.android.retrofitsample.net.track;

import androidx.annotation.Nullable;

import okhttp3.Request;

public interface INetTracker {
    void apiRequestInfoTrack(@Nullable Request request);
}
