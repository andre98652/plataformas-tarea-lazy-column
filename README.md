# 📘 Proyecto Jetpack Compose - Gestión de Cursos  
**Autor:** André David Delgado Allpan  

---

## 🧩 Descripción del proyecto

Este proyecto implementa una **interfaz en Android Jetpack Compose** que permite **agregar, editar y eliminar cursos** de una lista.  
La aplicación se basa en el patrón **MVVM**, utilizando un `ViewModel` (`CursosViewModel`) que administra el estado de los cursos mediante `mutableStateListOf` y `mutableStateOf`.

La pantalla principal contiene:

- Un **formulario** en la parte superior con tres campos: `Id`, `Nombre` y `Descripción`.
- Un conjunto de **botones** para agregar o limpiar el formulario.
- Una **lista de cursos dinámica** (LazyColumn) que se actualiza automáticamente cuando el usuario agrega, modifica o elimina elementos.

---

## ⚙️ Estructura principal del código

### 🧠 `CursosViewModel`
Es la clase encargada de manejar el estado de los cursos:
- Guarda los valores del formulario (`id`, `nombre`, `descripcion`).
- Almacena la lista de cursos en `mutableStateListOf<Curso>()`.
- Contiene funciones para agregar, editar, eliminar y limpiar datos.

### 🎨 `PantallaCursos`
Es la interfaz de usuario (*Composable*) que:
- Muestra el formulario con campos `OutlinedTextField`.
- Tiene un botón **Agregar / Guardar cambios** y otro para **Limpiar**.
- Muestra la lista de cursos usando un componente **LazyColumn**, que renderiza eficientemente los ítems visibles en pantalla.

---

## 🧱 Componentes importantes

### 🔹 `LazyColumn`
Es el equivalente en Compose a un RecyclerView, pero más simple y declarativo.  
Se usa para mostrar la lista de cursos en forma de tarjetas (`Card`), dentro de una columna que **permite desplazamiento (scroll)** automático.

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxWidth().weight(1f),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    itemsIndexed(vm.cursos) { index, curso ->
        Card { /* contenido del curso */ }
    }
}
```

El `LazyColumn` solo compone los elementos que están visibles, lo que mejora el rendimiento cuando la lista es larga (por ejemplo, 100 cursos generados en el ejemplo inicial).

---

### 🔹 `Scaffold`

El `Scaffold` organiza la pantalla con estructura de diseño moderna, integrando elementos como:

- El `SnackbarHost` (para mostrar mensajes flotantes).  
- El contenido principal (`Column`).  
- Espaciados y paddings adaptables.

Gracias al `Scaffold`, el `Snackbar` puede aparecer sobre la lista o el formulario sin superponerse de manera incorrecta.

---

## 🔄 Comportamiento y actualización

Cada vez que el usuario:

- Escribe en un campo → el `mutableStateOf` actualiza el valor en el ViewModel.  
- Agrega, edita o borra un curso → Compose **recompone automáticamente** la interfaz, mostrando el cambio.

---

## ❓ Pregunta:  
### ¿Por qué se requiere hacer *scrolling* para que se actualice el valor mostrado?

Esto ocurre debido a **cómo Jetpack Compose maneja la recomposición y el rendering de listas grandes** en `LazyColumn`.

**Explicación técnica:**
- `LazyColumn` solo *compone* los elementos visibles en pantalla para optimizar el rendimiento.  
- Los elementos que están fuera del viewport (fuera de la vista) no se actualizan hasta que vuelven a entrar en el área visible.  
- Por eso, si modificas un elemento que no está visible, el cambio no se refleja inmediatamente en pantalla… **hasta que haces scroll y Compose vuelve a "recomponer" ese ítem**.

**En resumen:**  
👉 El *scroll* fuerza a Compose a **recomponer los elementos invisibles**, lo cual hace que se muestren los valores actualizados.
