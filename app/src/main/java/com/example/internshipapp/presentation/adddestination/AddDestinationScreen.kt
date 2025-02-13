package com.example.internshipapp.presentation.adddestination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.internshipapp.presentation.components.DatePicker
import com.example.internshipapp.presentation.components.ListBottomSheet
import com.example.internshipapp.presentation.components.EditDialog
import com.example.internshipapp.utils.Constants.destList
import com.example.internshipapp.utils.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Destination
@Composable
fun AddDestScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel:AddDestViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddDestinationScreen(
        onBackButtonClick = { navigator.navigateUp() },
        snackBarEvent = viewModel.snackbarEventFlow,
        state = state,
        onEvent = viewModel::manageEventChanges,
        isSuccessful = {
            navigator.navigateUp()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDestinationScreen(
    onBackButtonClick: () -> Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    state: AddDestScreenState,
    onEvent:(AddDestScreenEvent)->Unit,
    isSuccessful:()->Unit
){
    var taskTitleError by rememberSaveable { mutableStateOf<String?>(null) }
    taskTitleError =when{
        state.title.isBlank() -> "Please enter the title."
        state.title.length<4 -> "Title is too short."
        state.title.length>30 -> "Title is too long."
        else -> null
    }
    var mapsLinkError by rememberSaveable { mutableStateOf<String?>(null) }
    mapsLinkError =when{
        state.mapsLink.isBlank() -> "Please enter the link."
        !isValidMapsUrl(state.mapsLink) -> "Enter valid Link."
        else -> null
    }
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState= rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isSubjectBottomSheetOpen by remember { mutableStateOf(false) }
    val subBottomSheetState= rememberModalBottomSheetState()
    val scope= rememberCoroutineScope()    //for dismissing the bottom sheet when subject is clicked
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

    DatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = { isDatePickerDialogOpen = false },
        onConfirmBtnClick = {
            onEvent(AddDestScreenEvent.onDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen = false
        }
    )
    ListBottomSheet(
        sheetState = subBottomSheetState,
        isOpen = isSubjectBottomSheetOpen,
        destinationList = destList,
        onDestinationClicked = {
            //aq to documentation
            scope.launch { subBottomSheetState.hide() }.invokeOnCompletion {
                if(!subBottomSheetState.isVisible) isSubjectBottomSheetOpen=false
            }
            onEvent(AddDestScreenEvent.onRelatedToDestChange( it))
        },
        onDismissRequest = {isSubjectBottomSheetOpen=false}
    )

    if(state.isSuccessfully){
        isSuccessful()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            AddDestinationTopAppBar(
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {isDeleteDialogOpen=true}
            ) 
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value =state.title,
                label = { Text(text = "Title") },
                onValueChange = { onEvent(AddDestScreenEvent.onTitleChange(it))},
                singleLine = true,
                isError = taskTitleError!=null && state.title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty()) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.desc,
                label = { Text(text = "Description") },
                onValueChange = { onEvent(AddDestScreenEvent.onDescChange(it))},
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.mapsLink,
                label = { Text(text = "Maps Link") },
                onValueChange = { onEvent(AddDestScreenEvent.onMapsLinkChange(it))},
                isError = mapsLinkError!=null && state.mapsLink.isNotBlank(),
                supportingText = { Text(text = mapsLinkError.orEmpty()) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Date", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.date.changeMillisToString(),  //extension function to convert millis to string
                    style = MaterialTheme.typography.bodyLarge,
                )
                IconButton(onClick = {isDatePickerDialogOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date "
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Related to Destination", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstDest= destList.firstOrNull()?.name?:""
                Text(
                    text = state.relatedToDest ?: firstDest, style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isSubjectBottomSheetOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }
            if(state.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .padding(16.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 8.dp
                )
            }
            if(!state.isLoading){
                Button(
                    enabled = taskTitleError==null && mapsLinkError==null,
                    onClick = {
                        onEvent(AddDestScreenEvent.saveDestination)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ){
                    Text(text = "Save")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDestinationTopAppBar(
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
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
        title={ Text(text = "Add Destination", style = MaterialTheme.typography.headlineSmall) },
    )
}

private fun isValidMapsUrl(link:String):Boolean{
    val urlRegex="""^(https?|ftp)://[\w\-]+(\.[\w\-]+)+[/#?]?.*$""".toRegex()
    return urlRegex.matches(link)
}

fun Long?.changeMillisToString():String{
    val date: LocalDate =this?.let{
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()   //if fails to convert return today date
    return date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
}