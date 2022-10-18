package com.dinder.rihla.rider.utils

import androidx.core.net.toUri
import com.dinder.rihla.rider.BuildConfig

object DynamicLinkUtils {
    fun getAppPrefix() =
        if (BuildConfig.DEBUG) "https://safraadebug.page.link" else "https://joinsafraa.page.link"

    fun invitationLink(id: String) = "${getAppPrefix()}/invites/?invitedBy=$id".toUri()
}
