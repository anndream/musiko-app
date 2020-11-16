package app.musiko

import android.support.v4.media.session.PlaybackStateCompat

object MusikoConstants {

    const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2588
    const val RESTORE_SETTINGS_FRAGMENT = "RESTORE_SETTINGS_FRAGMENT"

    // launched by, used to determine which MusicContainerListFragment is instantiated by the ViewPager
    const val ARTIST_VIEW = "0"
    const val ALBUM_VIEW = "1"
    const val FOLDER_VIEW = "2"

    // on list ended option
    const val CONTINUE = "0"

    // song visualization options
    const val TITLE = "0"

    // sorting
    const val DEFAULT_SORTING = 0
    const val DESCENDING_SORTING = 1
    const val ASCENDING_SORTING = 2
    const val TRACK_SORTING = 3
    const val TRACK_SORTING_INVERTED = 4

    // error tags
    const val TAG_NO_PERMISSION = "NO_PERMISSION"
    const val TAG_NO_MUSIC = "NO_MUSIC"
    const val TAG_NO_MUSIC_INTENT = "NO_MUSIC_INTENT"
    const val TAG_SD_NOT_READY = "SD_NOT_READY"

    // fragments tags
    const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT"
    const val ERROR_FRAGMENT_TAG = "ERROR_FRAGMENT"
    const val EQ_FRAGMENT_TAG = "EQ_FRAGMENT"

    // Player playing statuses
    const val PLAYING = PlaybackStateCompat.STATE_PLAYING
    const val PAUSED = PlaybackStateCompat.STATE_PAUSED
    const val RESUMED = PlaybackStateCompat.STATE_NONE

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_MUSIKO"
    const val NOTIFICATION_INTENT_REQUEST_CODE = 100
    const val NOTIFICATION_ID = 101
    const val FAST_FORWARD_ACTION = "FAST_FORWARD_MUSIKO"
    const val PREV_ACTION = "PREV_MUSIKO"
    const val PLAY_PAUSE_ACTION = "PLAY_PAUSE_MUSIKO"
    const val NEXT_ACTION = "NEXT_MUSIKO"
    const val REWIND_ACTION = "REWIND_MUSIKO"
    const val REPEAT_ACTION = "REPEAT_MUSIKO"
    const val CLOSE_ACTION = "CLOSE_MUSIKO"
}
