package com.example.supplelab.di

import com.example.supplelab.data.repository.AdminRepositoryImpl
import com.example.supplelab.data.repository.CustomerRepositoryImpl
import com.example.supplelab.data.repository.ProductRepositoryImpl
import com.example.supplelab.domain.repository.AdminRepository
import com.example.supplelab.domain.repository.CustomerRepository
import com.example.supplelab.domain.repository.ProductRepository
import org.koin.dsl.module
import kotlin.math.sin

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl() }
    single<AdminRepository> { AdminRepositoryImpl(get()) }
    single<ProductRepository>{ ProductRepositoryImpl() }
}