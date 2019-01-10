package com.forreview.expansion

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Binder
import android.os.Messenger
import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import com.android.vending.expansion.zipfile.ZipResourceFile
import com.google.android.vending.expansion.downloader.*
import com.forreview.helper.ResByName
import timber.log.Timber
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.CRC32

class DownloaderService : Service(), IDownloaderClient {

    private val binder = LocalBinder()

    private var mDownloaderClientStub: IStub? = null

    private var mRemoteService: IDownloaderService? = null

    private var validationTask: ValidationTask? = null

    val liveData = MutableLiveData<DownloaderViewState>()

    override fun onCreate() {
        super.onCreate()
        Timber.tag("DownloaderService").d("onCreate")

        if (!ResByName.isExpansionFilesDelivered(this)) {
            Timber.tag("DownloaderService").d("isExpansionFilesDelivered = false")
            try {
                val intentToLaunchThisActivityFromNotification = Intent(this@DownloaderService, DownloaderActivity::class.java)
                intentToLaunchThisActivityFromNotification.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                val pendingIntent = PendingIntent.getActivity(this@DownloaderService, 0, intentToLaunchThisActivityFromNotification, PendingIntent.FLAG_UPDATE_CURRENT)

                val startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent, ExpansionService::class.java)

                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                    Timber.tag("DownloaderService").w("DOWNLOAD_REQUIRED")
                    mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, ExpansionService::class.java)
                    mDownloaderClientStub?.connect(this)
                    return
                } else {
                    Timber.tag("DownloaderService").d("NO_DOWNLOAD_REQUIRED")
                    finishSuccess()
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e("Cannot find own package! MAYDAY!")
                Timber.e(e)
            }
        } else {
            Timber.tag("DownloaderService").d("isExpansionFilesDelivered = true")
            validateXAPKZipFiles()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelValidation()
        mDownloaderClientStub?.disconnect(this)
    }

    fun requestContinueDownloadWithCellular() {
        mRemoteService?.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR)
        mRemoteService?.requestContinueDownload()
    }

    fun requestContinueDownload() {
        mRemoteService?.requestContinueDownload()
    }

    fun requestPauseDownload() {
        mRemoteService?.requestPauseDownload()
    }

    private fun finishSuccess() {
        liveData.postValue(DownloaderViewState.Success)
        stopSelf()
    }

    private fun finishError() {
        liveData.postValue(DownloaderViewState.Error)
        stopSelf()
    }

    override fun onServiceConnected(messenger: Messenger) {
        Timber.tag("DownloaderService").d("onServiceConnected")
        mRemoteService = DownloaderServiceMarshaller.CreateProxy(messenger)
        mRemoteService?.onClientUpdated(mDownloaderClientStub?.messenger)
    }

    override fun onDownloadStateChanged(newState: Int) {
        Timber.tag("DownloaderService").e("onDownloadStateChanged")

        val messageId = Helpers.getDownloaderStringResourceIDFromState(newState)
        val message = getString(messageId)

        when (newState) {
            IDownloaderClient.STATE_IDLE -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_IDLE")
                // STATE_IDLE means the service is listening, so it's
                // safe to start making calls via mRemoteService.
                liveData.postValue(DownloaderViewState.Download.Idle(message))
            }
            IDownloaderClient.STATE_CONNECTING,
            IDownloaderClient.STATE_FETCHING_URL -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_CONNECTING")
                liveData.postValue(DownloaderViewState.Download.Connecting(message))
            }
            IDownloaderClient.STATE_DOWNLOADING -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_DOWNLOADING")
                liveData.postValue(DownloaderViewState.Download.Downloading(message))
            }
            IDownloaderClient.STATE_FAILED_CANCELED,
            IDownloaderClient.STATE_FAILED,
            IDownloaderClient.STATE_FAILED_FETCHING_URL,
            IDownloaderClient.STATE_FAILED_UNLICENSED -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_FAILED_CANCELED")
                liveData.postValue(DownloaderViewState.Download.Failed(message))
            }
            IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION,
            IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_PAUSED_NEED_CELLULAR_PERMISSION")
                liveData.postValue(DownloaderViewState.Download.CellularPermission(message))
            }
            IDownloaderClient.STATE_PAUSED_BY_REQUEST -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_PAUSED_BY_REQUEST")
                liveData.postValue(DownloaderViewState.Download.PausedByRequest(message))
            }
            IDownloaderClient.STATE_PAUSED_ROAMING,
            IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_PAUSED_ROAMING")
                liveData.postValue(DownloaderViewState.Download.PausedRoaming(message))
            }
            IDownloaderClient.STATE_COMPLETED -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged STATE_COMPLETED")
                liveData.postValue(DownloaderViewState.Download.Complete(message))
                mDownloaderClientStub?.disconnect(this)
                validateXAPKZipFiles()
            }
            else -> {
                Timber.tag("DownloaderService").d("onDownloadStateChanged else")

            }
        }
    }

    private fun validateXAPKZipFiles() {
        Timber.tag("DownloaderService").d("validateXAPKZipFiles")
        cancelValidation()
        validationTask = ValidationTask()
        validationTask?.execute()
    }



    override fun onDownloadProgress(progress: DownloadProgressInfo) {
        liveData.postValue(DownloaderViewState.Download.Progress(progress))
    }

    override fun onBind(intent: Intent) = binder

    fun cancelValidation() {
        validationTask?.isValidationCancelled = true
//        validationTask?.cancel(true)
    }

    inner class LocalBinder : Binder() {
        val service: DownloaderService
            get() = this@DownloaderService
    }

    inner class ValidationTask: AsyncTask<Any, DownloadProgressInfo, Boolean>() {

        var isValidationCancelled = false

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Any): Boolean? {
            for (xf in ResByName.xAPKS) {
                var fileName = Helpers.getExpansionAPKFileName(this@DownloaderService, xf.mIsMain, xf.mFileVersion)
                Timber.d("ExpansionAPKFileName $fileName")
                if (!Helpers.doesFileExist(this@DownloaderService, fileName, xf.mFileSize, false)) {
                    return false
                }

                fileName = Helpers.generateSaveFileName(this@DownloaderService, fileName)
                Timber.d("SaveFileName $fileName")
                val zrf: ZipResourceFile
                val buf = ByteArray(1024 * 256)
                try {
                    zrf = ZipResourceFile(fileName)
                    val entries = zrf.allEntries
                    Timber.d("allEntries ${entries.size}")
                    /**
                     * First calculate the total compressed length
                     */
                    var totalCompressedLength: Long = 0
                    for (entry in entries) {
                        totalCompressedLength += entry.mCompressedLength
                    }
                    var averageVerifySpeed = 0f
                    var totalBytesRemaining = totalCompressedLength
                    var timeRemaining: Long
                    /**
                     * Then calculate a CRC for every file in the Zip file,
                     * comparing it to what is stored in the Zip directory.
                     * Note that for compressed Zip files we must extract
                     * the contents to do this comparison.
                     */

                    Timber.e("ExternalFilesDir ${getExternalFilesDir(null)}")



                    for (entry in entries) {
                        if (-1L != entry.mCRC32) {
                            Timber.d("entry name ${entry.mFileName}")
                            var length = entry.mUncompressedLength
                            val crc = CRC32()
                            var dis: DataInputStream? = null
                            var fos: FileOutputStream? = null
                            var dir: File? = null
                            var file: File? = null
                            try {
                                dis = DataInputStream(zrf.getInputStream(entry.mFileName))

                                dir = File(getExternalFilesDir(null), "media")
                                dir.mkdirs()
                                file = File(dir, entry.mFileName)
//                                    file.createNewFile()
                                fos = file.outputStream()

                                var startTime = SystemClock.uptimeMillis()
                                while (length > 0) {
                                    val seek = if (length > buf.size) {
                                        buf.size
                                    } else {
                                        length.toInt()
                                    }

                                    dis.readFully(buf, 0, seek)
                                    crc.update(buf, 0, seek)
                                    fos.write(buf)
                                    length -= seek.toLong()
                                    val currentTime = SystemClock.uptimeMillis()
                                    val timePassed = currentTime - startTime
                                    if (timePassed > 0) {
                                        val currentSpeedSample = seek.toFloat() / timePassed.toFloat()
                                        if (0f != averageVerifySpeed) {
                                            averageVerifySpeed = SMOOTHING_FACTOR * currentSpeedSample + (1 - SMOOTHING_FACTOR) * averageVerifySpeed
                                        } else {
                                            averageVerifySpeed = currentSpeedSample
                                        }
                                        totalBytesRemaining -= seek.toLong()
                                        timeRemaining = (totalBytesRemaining / averageVerifySpeed).toLong()
                                        this.publishProgress(DownloadProgressInfo(totalCompressedLength, totalCompressedLength - totalBytesRemaining, timeRemaining, averageVerifySpeed))
                                    }
                                    startTime = currentTime

                                    if (isValidationCancelled) {
                                        return false
                                    }
                                }
                                if (crc.value != entry.mCRC32) {
                                    Timber.e("CRC does not match for entry: ${entry.mFileName}")
                                    Timber.e("In file: ${entry.zipFileName}")
                                    return false
                                }
                            } catch (exc: Exception) {
                                Timber.e(exc.message)
                            } finally {
                                dis?.close()
                                fos?.close()
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                }

            }
            return true
        }

        override fun onProgressUpdate(values: Array<DownloadProgressInfo>) {
            liveData.postValue(DownloaderViewState.Validating.Progress(values[0]))
        }

        override fun onPostExecute(isSuccess: Boolean) {
            Timber.tag("DownloaderService").d("Validation Completed $isSuccess")
            if (isSuccess) {
                liveData.postValue(DownloaderViewState.Validating.Success)
            } else {
                liveData.postValue(DownloaderViewState.Validating.Error)
            }
            stopSelf()
            super.onPostExecute(isSuccess)
        }
    }

    companion object {

        fun bind(context: Context, connection: ServiceConnection) {
            context.startService(Intent(context, DownloaderService::class.java))
            context.bindService(Intent(context, DownloaderService::class.java), connection, Context.BIND_AUTO_CREATE)
        }

        fun unbind(context: Context, connection: ServiceConnection) {
            context.unbindService(connection)
        }

        /**
         * Calculating a moving average for the validation speed so we don't get
         * jumpy calculations for time etc.
         */
        private const val SMOOTHING_FACTOR = 0.005f
    }
}
