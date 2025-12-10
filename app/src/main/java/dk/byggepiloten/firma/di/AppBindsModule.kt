package dk.byggepiloten.firma.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.FirmaPriceRepository
import dk.byggepiloten.firma.data.repository.MaterialRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import dk.byggepiloten.firma.data.repository.UserRepository
import dk.byggepiloten.firma.data.repository.impl.AuthRepositoryImpl
import dk.byggepiloten.firma.data.repository.impl.FirmaPriceRepositoryImpl
import dk.byggepiloten.firma.data.repository.impl.MaterialRepositoryImpl
import dk.byggepiloten.firma.data.repository.impl.RequestRepositoryImpl
import dk.byggepiloten.firma.data.repository.impl.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindFirmaPriceRepository(impl: FirmaPriceRepositoryImpl): FirmaPriceRepository

    @Binds @Singleton
    abstract fun bindMaterialRepository(impl: MaterialRepositoryImpl): MaterialRepository

    @Binds @Singleton
    abstract fun bindRequestRepository(impl: RequestRepositoryImpl): RequestRepository
}