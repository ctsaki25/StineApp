package com.example.bobbysandroidstartersolution

import java.util.Date


class Event(val name: String, eventDate: Date) {
    private val eventDate: Date

    init {
        this.eventDate = eventDate
    }

    fun getEventDate(): Date {
        return eventDate
    }
}

