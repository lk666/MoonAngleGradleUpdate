package cn.com.bluemoon.delivery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;

import cn.com.bluemoon.delivery.BuildConfig;
import cn.com.bluemoon.delivery.R;

/**
 * DIsplay image in imageView async.
 * Created by luok on 16-4-19.
 */
public class ImageLoaderUtil {
    private static ImageLoader mImageLoader;

    /**
     * 初始化
     */
    public static void init(Context context) {
        try {
            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    // TODO: lk 2016/6/6 default image
                    .showImageOnLoading(R.mipmap.loading_img_logo)
//                    .showImageForEmptyUri()
//                    .showImageOnFail()
                    .build();
            final File cacheDir = new File(Constants.PATH_CACHE);
            final long maxAgeTimeInSeconds = 7 * 24 * 60 * 60;   // 7 days cache
            final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder
                    (context)
                    .threadPoolSize(5)
//                    .memoryCacheSize()
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())
                    .diskCache(new LimitedAgeDiskCache(cacheDir, maxAgeTimeInSeconds))
                    .defaultDisplayImageOptions(options)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .build();
            ImageLoader.getInstance().init(config);
            L.writeLogs(!BuildConfig.RELEASE);
        } catch (Throwable e) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
            LogUtils.e(e.getMessage());
        }
    }

    public static void displayImage(Context context, String requestUrl, ImageView view) {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }

        mImageLoader.displayImage(requestUrl, view);
    }

    /**
     * @param context
     * @param requestUrl
     * @param view
     * @param loadingImgRec 加载中的图片
     */
    public static void displayImage(Context context, String requestUrl, ImageView view, int
            loadingImgRec) {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(loadingImgRec)
                .build();

        mImageLoader.displayImage(requestUrl, view, options);
    }

    /**
     * @param context
     * @param requestUrl
     * @param view
     * @param loadingImgRec 加载中的图片
     * @param errImgRec     加载失败的图片
     */
    public static void displayImage(Context context, String requestUrl, ImageView view, int
            loadingImgRec, int  errImgRec) {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(loadingImgRec)
                .showImageOnFail(errImgRec)
                .build();

        mImageLoader.displayImage(requestUrl, view, options);
    }
}
