package com.forreview.datamanager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Messenger
import com.google.android.vending.expansion.downloader.*
import com.forreview.base.BaseViewModel
import com.forreview.expansion.DownloaderActivity
import com.forreview.expansion.ExpansionService
import com.forreview.helper.CoroutineExecutor
import com.forreview.helper.ResByName
import com.forreview.helper.SchedulerProvider
import com.forreview.utils.launchSilent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class ExpansionViewModel: BaseViewModel() {
}

class ExpansionViewModelImpl(
    private val context: Context,
    private val executor: CoroutineExecutor,
    private val schedulerProvider: SchedulerProvider
): ExpansionViewModel(), IDownloaderClient {



    private var mDownloaderClientStub: IStub? = null

    private var validateJob: Job? = null

    override fun onCreateView() {
        start()
    }

    override fun onResume() {
        mDownloaderClientStub?.connect(context)
    }

    override fun onPause() {
        mDownloaderClientStub?.disconnect(context)
    }

    override fun onDestroyView() {
        validateJob?.cancel()
    }

    fun start() = launchSilent(executor.ui()) {
        if (!ResByName.isExpansionFilesDelivered(context)) {
            try {
                val intent = Intent(context, DownloaderActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(
                    context,
                    pendingIntent,
                    DownloaderActivity::class.java
                )

                if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
                    mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(
                        this@ExpansionViewModelImpl,
                        ExpansionService::class.java
                    )
                } else {
                    finishSuccess()
                }
            } catch (exc: PackageManager.NameNotFoundException) {
                Timber.e("Cannot find own package! MAYDAY!")
                Timber.e(exc)
            }
        } else {
            validateJob = validateXAPKZipFiles()
        }
    }

    private fun finishSuccess() {

    }

    private suspend fun validateXAPKZipFiles() = GlobalScope.launch(executor.io()) {



        /*for (xf in ResByName.xAPKS) {
            var fileName = Helpers.getExpansionAPKFileName(context, xf.mIsMain, xf.mFileVersion)
            if (!Helpers.doesFileExist(context, fileName, xf.mFileSize, false)) {
                return false
            }

            fileName = Helpers.generateSaveFileName(context, fileName)
            Timber.d("SaveFileName $fileName")
            val zrf: ZipResourceFile
            val buf = ByteArray(1024 * 256)
            try {
                zrf = ZipResourceFile(fileName)
                val entries = zrf.allEntries
                Timber.d("allEntries ${entries.size}")
                *//**
                 * First calculate the total compressed length
                 *//*
                var totalCompressedLength: Long = 0
                for (entry in entries) {
                    totalCompressedLength += entry.mCompressedLength
                }
                var averageVerifySpeed = 0f
                var totalBytesRemaining = totalCompressedLength
                var timeRemaining: Long
                *//**
                 * Then calculate a CRC for every file in the Zip file,
                 * comparing it to what is stored in the Zip directory.
                 * Note that for compressed Zip files we must extract
                 * the contents to do this comparison.
                 *//*

                Timber.e("ExternalFilesDir ${context.getExternalFilesDir(null)}")

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

                            dir = File(context.getExternalFilesDir(null), "media")
                            dir.mkdirs()
                            file = File(dir, entry.mFileName)
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
                                        averageVerifySpeed = SMOOTHING_FACTOR *
                                                currentSpeedSample + (1 - SMOOTHING_FACTOR) * averageVerifySpeed
                                    } else {
                                        averageVerifySpeed = currentSpeedSample
                                    }
                                    totalBytesRemaining -= seek.toLong()
                                    timeRemaining = (totalBytesRemaining / averageVerifySpeed).toLong()

                                    validationProgressLiveData.postValue(DownloadProgressInfo(
                                        totalCompressedLength,
                                        totalCompressedLength - totalBytesRemaining,
                                        timeRemaining,
                                        averageVerifySpeed
                                    ))
                                }
                                startTime = currentTime
                                if (mCancelValidation)
                                    return true
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

        }*/
    }

    override fun onServiceConnected(m: Messenger?) {
        TODO("not implemented")
    }

    override fun onDownloadStateChanged(newState: Int) {
        TODO("not implemented")
    }

    override fun onDownloadProgress(progress: DownloadProgressInfo?) {
        TODO("not implemented")
    }

    companion object {

        /**
         * Calculating a moving average for the validation speed so we don't get
         * jumpy calculations for time etc.
         */
        private const val SMOOTHING_FACTOR = 0.005f
    }
}