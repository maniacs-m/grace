package dog.woofwoofinc.grace

import android.app.Application
import android.util.Log

import com.crashlytics.android.Crashlytics
import com.tumblr.remember.Remember
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig

import io.fabric.sdk.android.Fabric

class GraceApplication : Application() {

    companion object {
        val TAG = GraceApplication::class.asTag()

        var instance: GraceApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Fabric.with(this, Crashlytics(), Twitter(twitterAuthConfig))
        Remember.init(applicationContext, "dog.woofwoofinc.grace")
    }

    val twitterAuthConfig: TwitterAuthConfig
        get() {
            try {
                val properties = assets.open("app.properties").readProperties()

                val key = properties.getProperty("twitter_key")
                val secret = properties.getProperty("twitter_secret")

                return TwitterAuthConfig(key, secret)
            } catch (e: Exception) {
                // It's not worth continuing to load the app because the Twitter application
                // key and secret are not available. But Crashlytics is not initialised yet to log
                // this exception, so create Fabric with just Crashlytics and hard crash
                Log.d(TAG, "Failed to key/secret from app.properties.", e)
                Fabric.with(this, Crashlytics())

                throw e
            }
        }
}
