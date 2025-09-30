package com.example.supplelab.di

import com.example.supplelab.data.repository.CustomerRepositoryImpl
import com.example.supplelab.domain.repository.CustomerRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
}