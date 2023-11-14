package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.formatters

fun String?.addCountryCodeIfMissing(): String? {
    if (this == null) return null
    return "+1${this.takeLast(10)}"
}