package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

data class Curso(val id: Int, val nombre: String, val descripcion: String)

class CursosViewModel : ViewModel() {
    val cursos = mutableStateListOf<Curso>()

    var id by mutableStateOf("")
        private set
    var nombre by mutableStateOf("")
        private set
    var descripcion by mutableStateOf("")
        private set

    // índice del elemento que se está editando; null = modo agregar
    var editIndex by mutableStateOf<Int?>(null)
        private set

    // === Renombrados para evitar choque con los setters generados ===
    fun actualizarId(v: String) { id = v }
    fun actualizarNombre(v: String) { nombre = v }
    fun actualizarDescripcion(v: String) { descripcion = v }

    fun limpiarFormulario() {
        id = ""
        nombre = ""
        descripcion = ""
        editIndex = null
    }

    fun seleccionarParaEditar(index: Int) {
        val c = cursos[index]
        id = c.id.toString()
        nombre = c.nombre
        descripcion = c.descripcion
        editIndex = index
    }

    fun eliminar(index: Int) {
        cursos.removeAt(index)
        if (editIndex == index) limpiarFormulario()
        else if (editIndex != null && index < editIndex!!) editIndex = editIndex!! - 1
    }

    fun guardar(onError: (String) -> Unit) {
        val idInt = id.toIntOrNull()
        if (idInt == null) { onError("El id debe ser número"); return }
        if (nombre.isBlank()) { onError("El nombre no puede estar vacío"); return }

        val nuevo = Curso(idInt, nombre.trim(), descripcion.trim())

        if (editIndex == null) {
            if (cursos.any { it.id == idInt }) {
                onError("Ya existe un curso con id $idInt"); return
            }
            cursos.add(nuevo)
            limpiarFormulario()
        } else {
            val i = editIndex!!
            cursos[i] = nuevo
            limpiarFormulario()
        }
    }

    fun cargarDummySiVacio() {
        if (cursos.isEmpty()) {
            repeat(5) { i ->
                cursos.add(Curso(i + 1, "Nombre ${i + 1}", "Descripción ${i + 1}"))
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PantallaCursos() } }
    }
}

@Composable
fun PantallaCursos(vm: CursosViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.cargarDummySiVacio() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // --- FORMULARIO ---
            OutlinedTextField(
                value = vm.id,
                onValueChange = vm::actualizarId,
                label = { Text("Id") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = vm.nombre,
                onValueChange = vm::actualizarNombre,
                label = { Text("Nombre del curso") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = vm.descripcion,
                onValueChange = vm::actualizarDescripcion,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val esEdicion = vm.editIndex != null
                Button(onClick = {
                    vm.guardar { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                }) {
                    Text(if (esEdicion) "Guardar cambios" else "Agregar")
                }
                OutlinedButton(onClick = vm::limpiarFormulario) { Text("Limpiar") }
            }

            Spacer(Modifier.height(20.dp))

            // --- LISTA ---
            Text(
                text = "Lista de cursos",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = vm.cursos,
                    key = { _, c -> c.id }
                ) { index, curso ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("(${curso.id}) ${curso.nombre}", style = MaterialTheme.typography.titleSmall)
                                if (curso.descripcion.isNotBlank()) {
                                    Text(curso.descripcion, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            IconButton(onClick = { vm.seleccionarParaEditar(index) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { vm.eliminar(index) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
