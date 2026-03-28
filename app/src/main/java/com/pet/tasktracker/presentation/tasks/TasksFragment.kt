package com.pet.tasktracker.presentation.tasks

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.pet.domain.models.Task
import com.pet.tasktracker.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 */
class TasksFragment : Fragment(R.layout.tasks_fragment) {

    lateinit var viewModel: TasksViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[TasksViewmodel::class.java]
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
                GetContent(viewModel)
            }
        }

        if (viewModel.tasks.value == null) {
            viewModel.loadTasks()
        }
    }
}

@Composable
fun GetContent(viewModel: TasksViewmodel) {
    var isDialogVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        TasksList(viewModel)
        ActionButtonAdd {
            isDialogVisible = true
        }
        if (isDialogVisible) {
            TaskDialog(viewModel) {
                isDialogVisible = false
            }
        }
    }
}


@Composable
fun TasksList(viewModel: TasksViewmodel) {
    val tasks by viewModel.tasks.observeAsState(emptyList())

    val listState = rememberLazyListState()

    // Запоминаем предыдущий размер списка
    val previousSize = remember { mutableIntStateOf(tasks.size) }
    LaunchedEffect(tasks.size) {
        // Если размер увеличился (добавили элемент)
        if (tasks.size > previousSize.intValue && previousSize.intValue > 0) {
            listState.scrollToItem(tasks.size - 1)
        }
        previousSize.intValue = tasks.size
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .systemBarsPadding()
    ) {
        items(items = tasks, key = { item: Task -> item.id!! }) { item: Task ->
            SwipeToDismissItem(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                onEdit = { isChecked ->
                    viewModel.editTask(item.copy(checked = isChecked))
                },
                onRemove = {
                    viewModel.deleteTask(item)
                }
            )
        }
    }
}

@Composable
fun ActionButtonAdd(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .offset(x = (-15).dp, y = (-15).dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = Color.Red,
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_input_add),
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun TaskDialog(
    viewmodel: TasksViewmodel,
    onDismiss: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }

    Dialog(onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text("Добавление задачи")
                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("") },
                    )
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss.invoke() },
                        content = { Text("Отмена") },
                    )
                    TextButton(
                        onClick = {
                            viewmodel.addTask(taskName)
                            onDismiss.invoke()
                        },
                        content = { Text("Добавить") },
                    )
                }
            }
        }
    }
}


@Composable
fun SwipeToDismissItem(
    item: Task,
    onRemove: () -> Unit,
    onEdit: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
//    val checkboxState = remember(item.id) { mutableStateOf(item.checked) }

    // Состояние, управляющее свайпом
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            // Если свайп завершен в направлении "конец -> начало" (слева направо или справа налево)
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    // Небольшая задержка для анимации
                    delay(300)
                    onRemove()
                }
                true // Подтверждаем изменение состояния
            } else {
                false // Отклоняем другие направления (например, свайп в другую сторону)
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        // Фон, который будет виден во время свайпа
        backgroundContent = {
            // Меняем цвет фона в зависимости от направления свайпа
            val backgroundColor = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                SwipeToDismissBoxValue.StartToEnd -> Color.Green
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {

        val isSwiping = dismissState.currentValue != SwipeToDismissBoxValue.Settled
        Surface(
            modifier = modifier
                .padding(vertical = 2.dp, horizontal = 4.dp)
                .border(
                    width = 2.dp,
                    color = Color.Blue,
                    shape = MaterialTheme.shapes.small
                ),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (isSwiping) 8.dp else 0.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .weight(0.8f)
                        .fillMaxWidth()
                )
                Checkbox(
                    checked = item.checked,
                    onCheckedChange = {
//                        checkboxState.value = it
//                        onEdit(checkboxState.value)
                        onEdit(it)
                    },
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth()
                )
            }

        }
    }
}