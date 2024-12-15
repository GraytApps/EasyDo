package com.example.easydo

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.easydo.adapter.TodoItemListener
import com.example.easydo.adapter.TodoListAdapter
import com.example.easydo.databinding.FragmentHomeBinding
import com.example.easydo.model.Todo
import com.example.easydo.util.MarginItemDecoration
import com.example.easydo.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), TodoItemListener {

    private var _binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recycler view setup
        registerForContextMenu(binding.recyclerView)
        binding.recyclerView.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.todo_list_item_margin)))

        // Listen for fab clicks
        binding.fab.setOnClickListener { _ ->
            findNavController().navigate(HomeFragmentDirections.newTodoAction())
        }

        // Listen and react to UI state changes emitted by Flow that is hosted in HomeViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeUiState.collect { state ->
                    val todoListAdapter =
                        TodoListAdapter(state.todoItems.toTypedArray(), this@HomeFragment)
                    binding.recyclerView.adapter = todoListAdapter

                    // Show the empty view if not items are saved
                    binding.emptyLayout.visibility = if (state.todoItems.isEmpty()) {
                         View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        }
    }

    override fun onCheckChanged(todo: Todo, checked: Boolean) {
        viewModel.setTodoCompleted(todo, checked)
    }

    override fun onClick(todo: Todo) {
        findNavController().navigate(HomeFragmentDirections.editTodoAction(todoId = todo.uid))
    }

    override fun onLongClick(todo: Todo) {
        viewModel.onContextMenuBeingShownFor(todo)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(R.string.action_edit)
        menu.add(R.string.action_delete)

        viewModel.todoForContextMenu?.let {
            if (!it.completed) {
                menu.add(R.string.action_complete)
            } else {
                menu.add(R.string.action_incomplete)
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            getString(R.string.action_edit) -> {
                viewModel.todoForContextMenu?.let {
                    findNavController().navigate(HomeFragmentDirections.editTodoAction(todoId = it.uid))
                }
                true
            }
            getString(R.string.action_delete) -> {
                viewModel.todoForContextMenu?.let {
                    viewModel.delete(todo = it)
                }
                true
            }
            getString(R.string.action_complete), getString(R.string.action_incomplete) -> {
                viewModel.todoForContextMenu?.let {
                    viewModel.setTodoCompleted(todo = it, completed = !it.completed)
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}