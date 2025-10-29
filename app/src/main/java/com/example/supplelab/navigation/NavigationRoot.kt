package com.example.supplelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supplelab.domain.model.ProductCategory
import com.example.supplelab.presentation.screens.admin.manageProduct.ManageProductScreen
import com.example.supplelab.presentation.screens.admin.AdminPanelScreen
import com.example.supplelab.presentation.screens.authentication.AuthScreen
import com.example.supplelab.presentation.screens.home.HomeScreen
import com.example.supplelab.presentation.screens.home.cart.checkout.CheckoutScreen
import com.example.supplelab.presentation.screens.home.cart.checkout.payment_completed.PaymentCompletedScreen
import com.example.supplelab.presentation.screens.home.category.category_search.CategorySearchScreen
import com.example.supplelab.presentation.screens.home.details.DetailsScreen
import com.example.supplelab.presentation.screens.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable


@Serializable
object AuthScreenKey: NavKey

@Serializable
object HomeScreenKey: NavKey

@Serializable
object ProfileScreenKey: NavKey

@Serializable
object AdminPanelScreenKey: NavKey

@Serializable
data class DetailsScreenKey(
    val id: String
) : NavKey

@Serializable
data class CategorySearchScreenKey(
    val category: String
) : NavKey

@Serializable
data class ManageProductScreenKey(
    val id: String? = null
) : NavKey

@Serializable
data class CheckoutScreenKey(
    val totalAmount: Double
) : NavKey

@Serializable
data class PaymentCompletedKey(
    val isSuccess: Boolean? = null,
    val error: String? = null
): NavKey




@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
){
    val isUserAuthenticated = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isUserAuthenticated) HomeScreenKey else AuthScreenKey
    val backStack = rememberNavBackStack(startDestination)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when(key){
                is AuthScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        AuthScreen(
                            onNavigateToHome = {
                                backStack.remove(key)
                                backStack.add(HomeScreenKey)
                            },
                            onNavigateToProfile = {
                                backStack.remove(key)
                                backStack.add(ProfileScreenKey)
                            }
                        )
                    }
                }
                is HomeScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        HomeScreen(
                            onSignOut = {
                                backStack.clear()
                                backStack.add(AuthScreenKey)
                            },
                            onProfileClick = {
                                backStack.add(ProfileScreenKey)
                            },
                            onAdminPanelClick = {
                                backStack.add(AdminPanelScreenKey)
                            },
                            onNavigateToDetails = { productId ->
                                backStack.add(DetailsScreenKey(id = productId))
                            },
                            onNavigateToCategory = { category ->
                                backStack.add(CategorySearchScreenKey(category = category))
                            },
                            onNavigateToCheckOut = {
                                backStack.add(CheckoutScreenKey(totalAmount = it))
                            }
                        )
                    }
                }
                is ProfileScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        ProfileScreen(
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            },
                            onNavigateToHome = {
                                backStack.remove(key)
                                backStack.add(HomeScreenKey)
                            }
                        )
                    }
                }
                is AdminPanelScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        AdminPanelScreen(
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            },
                            onNavigationToManageProduct = { id ->
                                backStack.add(ManageProductScreenKey(id))
                            }
                        )
                    }
                }
                is ManageProductScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        ManageProductScreen(
                            id = key.id,
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            }
                        )
                    }
                }

                is DetailsScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        DetailsScreen(
                            id = key.id,
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            }
                        )
                    }
                }
                is CategorySearchScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        CategorySearchScreen(
                            category = ProductCategory.valueOf(key.category),
                            onNavigationIconClicked = {
                                backStack.remove(key)
                            },
                            onNavigateToDetails = { productId ->
                                backStack.add(DetailsScreenKey(id = productId))
                            }
                        )
                    }
                }
                is CheckoutScreenKey -> {
                    NavEntry(
                        key = key,
                    ) {
                         CheckoutScreen(
                             totalAmount = key.totalAmount,
                                onNavigationIconClicked = {
                                    backStack.remove(key)
                                },
                             navigateToPaymentCompleted = { isSuccess, error ->
                                 backStack.add(
                                     PaymentCompletedKey(
                                         isSuccess = isSuccess,
                                         error = error
                                     )
                                 )

                             }
                         )
                    }
                }
                is PaymentCompletedKey -> {
                    NavEntry(
                        key = key,
                    ) {
                        PaymentCompletedScreen(
                            navigateBack = {
                                backStack.clear()
                                backStack.add(HomeScreenKey)
                            },
                            isSuccess = key.isSuccess,
                            error = key.error
                        )
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}