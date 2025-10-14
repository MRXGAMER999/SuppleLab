package com.example.supplelab.di

import com.example.supplelab.util.PhotoPicker
import org.koin.dsl.module

val utilModule = module {
    factory { PhotoPicker() }
}

