package com.forreview.expansion

import com.google.android.vending.expansion.downloader.impl.DownloaderService
import timber.log.Timber

// You must use the public key belonging to your publisher account
const val BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhA2RmoPpI/0rK821XO02JIrqJFyaZ06iiZoRRT2wcBBnf2U+mP0FE/TVKFtSm4eJyPm5SlfZPYi+WkFxWkujKGvuj4QZ43n5m+nyQXdcWbYp6Tw3rA5lkBp6m238hASunvamZFzlhLUC6yaajpIrhpDik/v9B0ATiD7nNiHDxaR44HBtXDrKedPx/GcyfQNgMbVZQr3bOji/XgU5Kn70ohUc+DC/pf1ykMw0QphYTStNWURiH76Q/VO/yIcMDBXZJLh6XR/bBeYbVLjORoG2bGpJ/EA1y/WJUYXg2UyX9kqf91eWjQRzNoKlQE0/x/b4IHBV5t3inJPa4kN3pvUCCwIDAQAB"
// You should also modify this salt
val SALT = byteArrayOf(
    99, 3, -12, -1, 54, 98, -100, -9, 43, 2,
    -8, -4, 44, 5, -100, -32, -33, 32, -1, 12
)

class ExpansionService : DownloaderService() {

    init {
        Timber.tag("DownloaderActivity").d("ExpansionService init")
    }

    override fun getPublicKey(): String = BASE64_PUBLIC_KEY

    override fun getSALT(): ByteArray = SALT

    override fun getAlarmReceiverClassName(): String = ExpansionAlarmReceiver::class.java.name
}
