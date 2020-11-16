package app.musiko.player

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import app.musiko.MusikoConstants
import app.musiko.R
import app.musiko.extensions.getCover
import app.musiko.extensions.toSpanned
import app.musiko.goPreferences
import app.musiko.helpers.ThemeHelper
import app.musiko.helpers.VersioningHelper
import app.musiko.ui.MainActivity

class MusicNotificationManager(private val playerService: PlayerService) {

    //notification manager/builder
    private val mNotificationManager = NotificationManagerCompat.from(playerService)
    private lateinit var mNotificationBuilder: NotificationCompat.Builder

    private val mNotificationActions
        @SuppressLint("RestrictedApi")
        get() = mNotificationBuilder.mActions

    private val sFastSeekingActions get() = goPreferences.isFastSeekingActions

    private var mAlbumArt = BitmapFactory.decodeResource(playerService.resources, R.drawable.album_art)

    private fun playerAction(action: String): PendingIntent {

        val pauseIntent = Intent()
        pauseIntent.action = action

        return PendingIntent.getBroadcast(
                playerService,
                MusikoConstants.NOTIFICATION_INTENT_REQUEST_CODE,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getFirstAdditionalAction() = if (sFastSeekingActions) {
        MusikoConstants.REWIND_ACTION
    } else {
        MusikoConstants.REPEAT_ACTION
    }

    private fun getSecondAdditionalAction() = if (sFastSeekingActions) {
        MusikoConstants.FAST_FORWARD_ACTION
    } else {
        MusikoConstants.CLOSE_ACTION
    }

    fun createNotification(): Notification {

        mNotificationBuilder = NotificationCompat.Builder(playerService, MusikoConstants.NOTIFICATION_CHANNEL_ID)

        if (VersioningHelper.isOreo()) {
            createNotificationChannel()
        }

        val openPlayerIntent = Intent(playerService, MainActivity::class.java)
        openPlayerIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent = PendingIntent.getActivity(
                playerService, MusikoConstants.NOTIFICATION_INTENT_REQUEST_CODE,
                openPlayerIntent, 0
        )

        mNotificationBuilder
                .setShowWhen(false)
                .setStyle(
                        MediaStyle()
                                .setShowActionsInCompactView(1, 2, 3)
                                .setMediaSession(playerService.getMediaSession().sessionToken)
                )
                .setContentIntent(contentIntent)
                .addAction(notificationAction(getFirstAdditionalAction()))
                .addAction(notificationAction(MusikoConstants.PREV_ACTION))
                .addAction(notificationAction(MusikoConstants.PLAY_PAUSE_ACTION))
                .addAction(notificationAction(MusikoConstants.NEXT_ACTION))
                .addAction(notificationAction(getSecondAdditionalAction()))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        updateNotificationContent()
        return mNotificationBuilder.build()
    }

    fun cancelNotification() {
        mNotificationManager.cancel(MusikoConstants.NOTIFICATION_ID)
    }

    fun updateNotification() {
        mNotificationManager
                .notify(
                        MusikoConstants.NOTIFICATION_ID,
                        mNotificationBuilder.build()
                )
    }

    fun onUpdateDefaultAlbumArt(bitmapRes: Bitmap, updateNotification: Boolean) {
        mAlbumArt = bitmapRes
        if (updateNotification) {
            onHandleNotificationUpdate(false)
        }
    }

    fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean) {
        if (::mNotificationBuilder.isInitialized) {
            if (!isAdditionalActionsChanged) {
                updateNotificationContent()
                updateNotification()
            } else {
                mNotificationActions[0] =
                        notificationAction(getFirstAdditionalAction())
                mNotificationActions[4] =
                        notificationAction(getSecondAdditionalAction())
                updateNotification()
            }
        }
    }

    fun updateNotificationContent() {
        val mediaPlayerHolder = playerService.mediaPlayerHolder
        mediaPlayerHolder.currentSong.first?.let { song ->

            val cover = if (goPreferences.isCovers) {
                song.getCover(playerService) ?: mAlbumArt
            } else {
                mAlbumArt
            }

            mNotificationBuilder.setContentText(
                    playerService.getString(
                            R.string.artist_and_album,
                            song.artist,
                            song.album
                    )
            )
                    .setContentTitle(
                            playerService.getString(
                                    R.string.song_title_notification,
                                    song.title
                            ).toSpanned()
                    )
                    .setLargeIcon(cover)
                    .setColorized(true)
                    .setSmallIcon(getNotificationSmallIcon(mediaPlayerHolder))
        }
    }

    private fun getNotificationSmallIcon(mediaPlayerHolder: MediaPlayerHolder) =
            when (mediaPlayerHolder.launchedBy) {
                MusikoConstants.FOLDER_VIEW -> R.drawable.ic_folder
                MusikoConstants.ALBUM_VIEW -> R.drawable.ic_library_music
                else -> R.drawable.ic_music_note
            }

    fun updatePlayPauseAction() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationActions[2] =
                    notificationAction(MusikoConstants.PLAY_PAUSE_ACTION)
        }
    }

    fun updateRepeatIcon() {
        if (::mNotificationBuilder.isInitialized && !sFastSeekingActions) {
            mNotificationActions[0] =
                    notificationAction(MusikoConstants.REPEAT_ACTION)
            updateNotification()
        }
    }

    private fun notificationAction(action: String): NotificationCompat.Action {
        var icon =
                if (playerService.mediaPlayerHolder.state != MusikoConstants.PAUSED) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
        when (action) {
            MusikoConstants.REPEAT_ACTION -> icon =
                    ThemeHelper.getRepeatIcon(playerService.mediaPlayerHolder)
            MusikoConstants.PREV_ACTION -> icon = R.drawable.ic_skip_previous
            MusikoConstants.NEXT_ACTION -> icon = R.drawable.ic_skip_next
            MusikoConstants.CLOSE_ACTION -> icon = R.drawable.ic_close
            MusikoConstants.FAST_FORWARD_ACTION -> icon = R.drawable.ic_fast_forward
            MusikoConstants.REWIND_ACTION -> icon = R.drawable.ic_fast_rewind
        }
        return NotificationCompat.Action.Builder(icon, action, playerAction(action)).build()
    }

    @RequiresApi(26)
    private fun createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(MusikoConstants.NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel(
                    MusikoConstants.NOTIFICATION_CHANNEL_ID,
                    playerService.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = playerService.getString(R.string.app_name)
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                mNotificationManager.createNotificationChannel(this)
            }
        }
    }
}
