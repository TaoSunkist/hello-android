package com.zhimeng.battery.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.fotoapparat.facedetector.FaceDetector;
import io.fotoapparat.facedetector.Rectangle;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;

public class FaceDetectorProcessorV2 implements FrameProcessor {

    private static Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    private final FaceDetector faceDetector;
    private final OnFacesDetectedListener listener;

    private FaceDetectorProcessorV2(Builder builder) {
        faceDetector = FaceDetector.create(builder.context);
        listener = builder.listener;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    @Override
    public void process(@NotNull Frame frame) {
        final List<Rectangle> faces = faceDetector.detectFaces(
                frame.getImage(),
                frame.getSize().width,
                frame.getSize().height,
                frame.getRotation()
        );

        MAIN_THREAD_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                listener.onFacesDetected(faces);
            }
        });
    }

    /**
     * Notified when faces are detected.
     */
    public interface OnFacesDetectedListener {

        /**
         * Null-object for {@link OnFacesDetectedListener}.
         */
        OnFacesDetectedListener NULL = new OnFacesDetectedListener() {
            @Override
            public void onFacesDetected(List<Rectangle> faces) {
                // Do nothing
            }
        };

        /**
         * Called when faces are detected. Always called on the main thread.
         *
         * @param faces detected faces. If no faces were detected - an empty list.
         */
        void onFacesDetected(List<Rectangle> faces);

    }

    /**
     * Builder for {@link io.fotoapparat.facedetector.processor.FaceDetectorProcessor}.
     */
    public static class Builder {

        private final Context context;
        private OnFacesDetectedListener listener = OnFacesDetectedListener.NULL;

        private Builder(Context context) {
            this.context = context;
        }

        /**
         * @param listener which will be notified when faces are detected.
         */
        public Builder listener(OnFacesDetectedListener listener) {
            this.listener = listener != null
                    ? listener
                    : OnFacesDetectedListener.NULL;

            return this;
        }

        public FaceDetectorProcessorV2 build() {
            return new FaceDetectorProcessorV2(this);
        }

    }

}
