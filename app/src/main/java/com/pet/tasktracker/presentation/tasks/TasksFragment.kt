package com.pet.tasktracker.presentation.tasks

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.pet.tasktracker.R


/**
 */
class TasksFragment : Fragment(R.layout.tasks_fragment) {

    lateinit var viewModel: TasksViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider.create(this)[TasksViewmodel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        // Правильный синтаксис для lifecycle-runtime 2.6.0+
        composeView.setViewTreeLifecycleOwner(viewLifecycleOwner)
        composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                getContent()
            }
        }

        if (viewModel.tasks.value == null) {
            viewModel.loadTasks()
        }
    }

    @Composable
    fun getContent() {

        val tasks = viewModel.tasks.observeAsState(emptyList())

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White)
                .systemBarsPadding()
        ) {
            items(items = tasks.value, key = { str: String -> str }) { item: String ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}