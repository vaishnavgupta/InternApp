package com.example.internshipapp.presentation.registration

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.internshipapp.R
import com.example.internshipapp.presentation.destinations.LoginScreenRouteDestination
import com.example.internshipapp.presentation.login.LoginScreenRoute
import com.example.internshipapp.utils.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun RegistrationScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel : RegistrationScreenViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegistrationScreen(
        state = state,
        onEvent = viewModel::manageEventChanges,
        snackBarEvent = viewModel.snackbarEventFlow,
        onLoginBtnClick = {
            navigator.navigate(LoginScreenRouteDestination())
        }
    )
}

@Composable
fun RegistrationScreen(
    state: RegisterScreenState,
    onEvent:(RegistrationScreenEvent)->Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onLoginBtnClick:()->Unit
) {
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    nameError=when{
        state.userName.length<2 && state.userName!=""  -> "Name is too short."
        state.userName.length>20 -> "Name is too long."
        else -> null
    }

    passwordError=when{
        state.userPassword.length<6  && state.userPassword!=""-> "Password is too short."
        state.userPassword.length>15  -> "Password is too long."
        else -> null
    }

    emailError=when{
        !Patterns.EMAIL_ADDRESS.matcher(state.userEmail).matches() && state.userEmail!=""-> "Enter a valid email"
        else -> null
    }

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
                }
            }

        }
    }

    Scaffold(
        topBar = { RegistrationScreenTopBar() },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { paddingValues ->
        Column (
            modifier= Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(R.drawable.login),
                contentDescription = "registration"
            )
            RegistrationTextFields(
                value = state.userName,
                onValueChanged = { onEvent(RegistrationScreenEvent.onUserNameChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                label = {
                    Text(text = "Enter your name")
                },
                isError = (state.userName.isNotBlank() && nameError!=null),
                errorMessage = nameError.orEmpty()
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            RegistrationTextFields(
                value = state.userEmail,
                onValueChanged = { onEvent(RegistrationScreenEvent.onUserEmsilChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = {
                    Text(text = "Enter your email")
                },
                isError = (state.userEmail.isNotBlank() && emailError!=null),
                errorMessage = emailError.orEmpty()
            )
            Spacer(modifier = Modifier.height(height = 10.dp))
            RegistrationTextFields(
                value = state.userPassword,
                onValueChanged = { onEvent(RegistrationScreenEvent.onUserPasswordChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                label = {
                    Text(text = "Enter your password")
                },
                applyVisualTransformation = true,
                isError = (state.userPassword.isNotBlank() && passwordError!=null),
                errorMessage = passwordError.orEmpty()
            )
            Spacer(modifier = Modifier.height(height = 40.dp))
            if(state.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp).padding(16.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 8.dp
                )
            }
            if(!state.isLoading){
                Button(
                    onClick = {onEvent(RegistrationScreenEvent.registerUser)},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    enabled = state.userName!="" && state.userEmail!="" && state.userPassword!="" && emailError==null && passwordError==null && nameError==null
                ) {
                    Text(text = "Register")
                }
            }
            Spacer(modifier = Modifier.height(height = 30.dp))
            TextButton(
                onClick = onLoginBtnClick
            ) {
                Text(text = "Already Registered? Login")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Registration", style = MaterialTheme.typography.headlineMedium)
        }
    )
}

@Composable
fun RegistrationTextFields(
    value: String,
    onValueChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    label: @Composable (() -> Unit)?,
    applyVisualTransformation: Boolean = false,
    isError:Boolean,
    errorMessage:String=""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        label = label,
        visualTransformation = if (applyVisualTransformation) PasswordVisualTrans() else VisualTransformation.None,
        isError = isError,
        supportingText = { Text(text = errorMessage) }
    )
}

private class PasswordVisualTrans : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            AnnotatedString("*".repeat(text.text.length)),
            OffsetMapping.Identity
        )
    }
}