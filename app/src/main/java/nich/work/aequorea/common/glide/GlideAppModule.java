package nich.work.aequorea.common.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.MemoryCacheAdapter;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public final class GlideAppModule extends AppGlideModule {
    
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        
        final RequestOptions options = new RequestOptions()
            .format(DecodeFormat.PREFER_ARGB_8888)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop();
        
        builder.setDefaultRequestOptions(options)
            .setDefaultTransitionOptions(Drawable.class, DrawableTransitionOptions.withCrossFade())
            .setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context))
            .setMemoryCache(new MemoryCacheAdapter());
    }
}
