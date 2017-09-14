package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.drawable.GifDrawable;
import com.zzhoujay.richtext.ext.ImageKit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/2/21.
 * 图片解析工具
 */
abstract class SourceDecode<T> {
    
    public static SourceDecode<byte[]> BASE64_SOURCE_DECODE = new SourceDecode<byte[]>() {

        @Override
        public void decodeSize(byte[] bytes, BitmapFactory.Options options) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }

        @Override
        public ImageWrapper decodeAsBitmap(byte[] bytes, BitmapFactory.Options options) {
            return ImageWrapper.createAsBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options));
        }

        @Override
        public ImageWrapper decodeAsGif(byte[] bytes, BitmapFactory.Options options) {
            return ImageWrapper.createAsGif(new GifDrawable(Movie.decodeByteArray(bytes, 0, bytes.length), options.outHeight, options.outWidth));
        }

        @Override
        public boolean isGif(byte[] bytes, BitmapFactory.Options options) {
            return ImageKit.isGif(bytes);
        }
    };
    
    public static SourceDecode<String> LOCAL_FILE_SOURCE_DECODE = new SourceDecode<String>() {

        @Override
        public void decodeSize(String s, BitmapFactory.Options options) {
            BitmapFactory.decodeFile(s, options);
        }

        @Override
        public ImageWrapper decodeAsBitmap(String s, BitmapFactory.Options options) {
            return ImageWrapper.createAsBitmap(BitmapFactory.decodeFile(s, options));
        }

        @Override
        public ImageWrapper decodeAsGif(String s, BitmapFactory.Options options) {
            return ImageWrapper.createAsGif(new GifDrawable(Movie.decodeFile(s), options.outHeight, options.outWidth));
        }

        @Override
        public boolean isGif(String s, BitmapFactory.Options options) {
            return ImageKit.isGif(s);
        }
    };
    
    public static SourceDecode<InputStream> REMOTE_SOURCE_DECODE = new SourceDecode<InputStream>() {

        private static final int MARK_POSITION = 1024 * 1024;

        @Override
        public void decodeSize(InputStream inputStream, BitmapFactory.Options options) {
            BufferedInputStream stream;
            if (inputStream instanceof BufferedInputStream) {
                stream = (BufferedInputStream) inputStream;
            } else {
                stream = new BufferedInputStream(inputStream);
            }
            if (options.inJustDecodeBounds) {
                stream.mark(MARK_POSITION);
            }
            BitmapFactory.decodeStream(stream, null, options);
            if (options.inJustDecodeBounds) {
                try {
                    stream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public ImageWrapper decodeAsBitmap(InputStream inputStream, BitmapFactory.Options options) {
            BufferedInputStream stream;
            if (inputStream instanceof BufferedInputStream) {
                stream = (BufferedInputStream) inputStream;
            } else {
                stream = new BufferedInputStream(inputStream);
            }
            if (options.inJustDecodeBounds) {
                stream.mark(MARK_POSITION);
            }
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            if (options.inJustDecodeBounds) {
                try {
                    stream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return ImageWrapper.createAsBitmap(bitmap);
        }

        @Override
        public ImageWrapper decodeAsGif(InputStream inputStream, BitmapFactory.Options options) {
            return ImageWrapper.createAsGif(new GifDrawable(Movie.decodeStream(inputStream), options.outHeight, options.outWidth));
        }

        @Override
        public boolean isGif(InputStream inputStream, BitmapFactory.Options options) {
            return ImageKit.isGif(inputStream);
        }
    };
    
    public ImageWrapper decode(ImageHolder holder, T t, BitmapFactory.Options options) {
        if (holder.isAutoPlay() && (holder.isGif() || isGif(t, options))) {
            holder.setIsGif(true);
            return decodeAsGif(t, options);
        } else {
            return decodeAsBitmap(t, options);
        }
    }
    
    public abstract boolean isGif(T t, BitmapFactory.Options options);
    
    public abstract void decodeSize(T t, BitmapFactory.Options options);
    
    public abstract ImageWrapper decodeAsBitmap(T t, BitmapFactory.Options options);
    
    public abstract ImageWrapper decodeAsGif(T t, BitmapFactory.Options options);

}
