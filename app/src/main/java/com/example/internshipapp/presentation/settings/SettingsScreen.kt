package com.example.internshipapp.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.internshipapp.R
import com.example.internshipapp.domain.models.User
import com.example.internshipapp.presentation.components.AboutDevDialog
import com.example.internshipapp.presentation.components.ChangeDetailsDialog
import com.example.internshipapp.presentation.components.InviteFriendDialog
import com.example.internshipapp.presentation.destinations.LoginScreenRouteDestination
import com.example.internshipapp.utils.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


@Destination
@Composable
fun SettingsScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        onBackButtonClick = {
            navigator.navigateUp()
        },
        snackBarEvent = viewModel.snackbarEventFlow,
        state = state,
        onEvent = viewModel::manageEventChanges,
        isSignOut = {
            navigator.navigate(LoginScreenRouteDestination())
        },
    )
}

@Composable
private fun SettingsScreen(
    onBackButtonClick: () -> Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    state: SettingsScreenState,
    onEvent:(SettingsScreenEvent)->Unit,
    isSignOut:()->Unit,
) {
    val snackBarHostState= remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event->
            when(event){
                is SnackBarEvent.ShowSnackbar -> {
                    snackBarHostState.showSnackbar(
                        message = event.msg,
                        duration = event.snackBarDuration
                    )
                }

                SnackBarEvent.navigateUp -> {
                    onBackButtonClick()
                }
            }

        }
    }

    var isInviteFriendDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isChangeUserDetailDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isAboutDeveloperDialog by rememberSaveable { mutableStateOf(false) }

    InviteFriendDialog(
        isOpen = isInviteFriendDialogOpen,
        phNum = state.friendMobile,
        onPhNumChange = { onEvent(SettingsScreenEvent.onFriendNumChange(it)) },
        onDismissRequest = { isInviteFriendDialogOpen = false },
        onConfirmClick = {
            onEvent(SettingsScreenEvent.sendInviteSms)
            isInviteFriendDialogOpen=false
        }
    )
    AboutDevDialog(
        isOpen = isAboutDeveloperDialog,
        onDismissRequest = {isAboutDeveloperDialog=false},
        onConfirmClick = {isAboutDeveloperDialog=false}
    )
    ChangeDetailsDialog(
        isOpen = isChangeUserDetailDialogOpen,
        name = state.newUserNmae,
        onUserNameChange = {
            onEvent(SettingsScreenEvent.onUserNameChange(it))
        },
        onDismissRequest = {isChangeUserDetailDialogOpen=false},
        onConfirmClick = {
            onEvent(SettingsScreenEvent.updateDetails)
            isChangeUserDetailDialogOpen=false
        },
    )

    //logout
    if(state.isSignOut){
        isSignOut()
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            SettingsScreenTopAppBar(
                onBackButtonClick = onBackButtonClick
            )
        }
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                UserDetailsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    userData = state.userData
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    ClickableCardSection(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        onClick = {isInviteFriendDialogOpen=true},
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        title = "Invite Friends"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ClickableCardSection(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        onClick = {isChangeUserDetailDialogOpen=true},
                        imageVector = Icons.Default.AccountCircle,
                        title = "Change Details"
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    ClickableCardSection(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        onClick = {
                            isAboutDeveloperDialog=true
                        },
                        imageVector = Icons.Default.Face,
                        title = "About Developer"
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                    ClickableCardSection(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        onClick = {
                            onEvent(SettingsScreenEvent.onLogoutClick)
                        },
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Logout"
                    )

                }
            }

        }
    }
}
@Composable
fun CircularImageView(){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val painter = painterResource(R.drawable.user2)
        Card(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(18.dp)
        ) {
            Image(
                painter=painter,
                contentDescription = "user Image",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun UserDetailsSection(modifier: Modifier = Modifier,userData:User?) {
    Column(
        modifier = modifier
    ) {
        CircularImageView()
        Spacer(modifier=Modifier.height(8.dp))
        if (userData != null) {
            Text(
                text = userData.userName?.ifBlank { "Guest User" } ?: "Guest User" ,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier=Modifier.height(4.dp))
        if (userData != null) {
            Text(
                text = userData.userEmail?.ifBlank { "Guest User" } ?: "Guest User" ,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenTopAppBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        title={ Text(text = "Settings", style = MaterialTheme.typography.headlineSmall) },
    )
}

@Composable
fun ClickableCardSection(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    title:String
    ) {
    ElevatedCard(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageVector = imageVector,
                    contentDescription = title,
                )
                Spacer(modifier=Modifier.height(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }
}
