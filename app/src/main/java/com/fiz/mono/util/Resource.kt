package com.fiz.mono.util

data class Resource<out T>(val status: Status, val message: String?) {

    companion object {

        fun <T> success(): Resource<T> {
            return Resource(Status.SUCCESS, null)
        }

        fun <T> error(msg: String): Resource<T> {
            return Resource(Status.ERROR, msg)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null)
        }

    }

}