package com.microservices.service;

import com.microservices.client.CategoriaClient;
import com.microservices.client.ProductoResponse;
import com.microservices.dto.Categoria;
import com.microservices.model.Producto;
import com.microservices.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaClient categoriaClient; // Inyección del cliente Feign

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    // Método para obtener el producto con la categoría enriquecida
    public Optional<ProductoResponse> findByIdWithCategoria(Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isEmpty()) {
            return Optional.empty();
        }

        Producto producto = productoOptional.get();

        // 1. Llamada al microservicio de Categoría usando Feign
        Categoria categoria = categoriaClient.obtenerCategoria(producto.getCategoriaId());

        // 2. Construir la respuesta (Wrapper)
        ProductoResponse response = new ProductoResponse();
        response.setProducto(producto);
        response.setCategoria((CategoriaClient) categoria); // Aquí usamos el objeto Categoria (DTO)

        return Optional.of(response);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}