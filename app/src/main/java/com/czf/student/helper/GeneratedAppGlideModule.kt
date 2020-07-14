package com.czf.student.helper

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GeneratedAppGlideModule:AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}