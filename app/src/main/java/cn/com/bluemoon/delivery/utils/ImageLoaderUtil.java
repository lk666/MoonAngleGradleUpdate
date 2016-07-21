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
import cn.com.bluemoon.lib.utils.threadhelper.ThreadPool;

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
                    .taskExecutor(ThreadPool.PICTURE_THREAD_POOL)
                    // 设置加载和显示内存缓存或者本地缓存图片的线程池，设置此项后threadPoolSize、threadPriority、tasksProcessingOrder会失效
                    // 由于加载和显示内存缓存或者本地缓存图片很快，因此与taskExecutor最好分开
                    .taskExecutorForCachedImages(ThreadPool.GENERAL_THREAD_POOL)
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

    // TODO: lk 2016/7/19 去除 context参数
    public static void displayImage(Context context, String requestUrl, ImageView view) {
        if (mImageLoader == null) {
            mImageLoader = ImageLoader.getInstance();
        }

        mImageLoader.displayImage(requestUrl, view);
    }

    /**
     * 从drawable中异步加载本地图片
     *
     * @param imageId
     * @param imageView
     */
    public static void displayImageMipmap(int imageId, ImageView imageView) {
        ImageLoader.getInstance().displayImage("drawable://" + imageId, imageView);
    }

    /**
     * @param requestUrl
     * @param view
     * @param loadingImgRec 加载中的图片
     */
    public static void displayImage(String requestUrl, ImageView view, int
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
     * @param requestUrl
     * @param view
     * @param loadingImgRec 加载中的图片
     * @param errImgRec     加载失败的图片
     */
    public static void displayImage(String requestUrl, ImageView view, int
            loadingImgRec, int errImgRec) {
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
