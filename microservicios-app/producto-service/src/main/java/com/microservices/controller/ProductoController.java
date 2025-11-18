package com.microservices.controller;

import com.microservices.client.ProductoResponse;
import com.microservices.model.Producto;
import com.microservices.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 1. LISTAR TODOS LOS PRODUCTOS
    @GetMapping
    public List<Producto> listar() {
        return productoService.findAll();
    }

    // 2. BUSCAR PRODUCTO POR ID (CON CATEGORÍA ENRIQUECIDA)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<ProductoResponse> response = productoService.findByIdWithCategoria(id);

        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. CREAR PRODUCTO
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // 4. ACTUALIZAR PRODUCTO
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datosProducto) {
        Optional<Producto> productoOptional = productoService.findById(id);

        return productoOptional.map(productoExistente -> {
                    // Actualizar campos
                    productoExistente.setNombre(datosProducto.getNombre());
                    productoExistente.setPrecio(datosProducto.getPrecio());
                    productoExistente.setCategoriaId(datosProducto.getCategoriaId());

                    // Asumiendo que el campo 'stock' también existe en tu entidad Producto
                    // productoExistente.setStock(datosProducto.getStock());

                    Producto productoActualizado = productoService.save(productoExistente);
                    return ResponseEntity.ok(productoActualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5. ELIMINAR PRODUCTO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}