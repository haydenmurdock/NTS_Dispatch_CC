import android.app.Application
import com.squareup.sdk.reader.ReaderSdk

class DriverSquarePaymentPortal : Application() {

    override fun onCreate() {
        super.onCreate()
        ReaderSdk.initialize(this)
    }
}