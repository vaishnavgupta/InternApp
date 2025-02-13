package com.example.internshipapp.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.internshipapp.domain.models.BottomNavItem
import com.example.internshipapp.presentation.destinations.AddDestScreenRouteDestination
import com.example.internshipapp.presentation.destinations.SettingsScreenRouteDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun HomeScreenRoute(navigator: DestinationsNavigator) {
    HomeScreen(
        onAddDestBtnClick = {
            navigator.navigate(AddDestScreenRouteDestination())
        },
        onSettingsClick = {
            navigator.navigate(SettingsScreenRouteDestination())
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddDestBtnClick:()->Unit,
    onSettingsClick: () -> Unit
) {

    val listState= rememberLazyListState()
    val isFABExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex==0 }  //for fab to reduce when 0th idx is passed
    }
    val scrollBehaviour=TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    val navItemList = listOf(
        BottomNavItem("Cafes",Icons.Rounded.Place),
        BottomNavItem("Places",Icons.Filled.Home),
        BottomNavItem("Events",Icons.Filled.Favorite)
    )

    var selectedIdx by remember {
        mutableIntStateOf(0)
    }

    Scaffold(

        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            HomeScreenTopAppBar(
                title = "Destinations",
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddDestBtnClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add destination") },
                text = { Text(text = "Add") },
                expanded = isFABExpanded
            )
        },
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIdx==index,
                        onClick = {
                            selectedIdx=index
                        },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) {
        HomeContentScreen(
            modifier = Modifier
                .padding(it),
            selectedIdx,
        )

    }
}

@Composable
fun HomeContentScreen(modifier: Modifier, selectedIdx: Int) {
    when(selectedIdx){
        0 -> CafesScreen(modifier)
        1 -> PlacesScreen(modifier)
        2 -> EventsScreen(modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(
    title:String,
    onSettingsClick:()->Unit,
) {
    TopAppBar(
        title = {
            Text(text =title, style = MaterialTheme.typography.headlineMedium)
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}


 