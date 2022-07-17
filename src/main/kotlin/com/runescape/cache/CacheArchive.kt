package com.runescape.cache

enum class CacheArchive(val index: Int) {
    MODELS_STANDARD(1),
    ANIMATIONS_STANDARD(2),
    MUSIC_STANDARD(3),
    MAPS_STANDARD(4),
    TEXTURES(5),
    SPRITES_RUNESCAPE(6);

    override fun toString(): String {
        return  "${super.toString()} {" + "index=" + index + '}'
    }

    /**
     * Gets the index that the file extension uses (the same index minus one).
     */
    open fun getFileIndex() = index - 1


    companion object {
        fun forFileIndex(index: Int) = values().firstOrNull { it.getFileIndex() == index } ?: error("Unable to find index $index")
        fun forArchiveIndex(index: Int) = values().firstOrNull { it.index == index } ?: error("Unable to find index $index")
    }
}