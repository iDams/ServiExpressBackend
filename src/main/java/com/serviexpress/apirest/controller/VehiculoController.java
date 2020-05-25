package com.serviexpress.apirest.controller;

import java.util.List;

import javax.validation.Valid;

import com.serviexpress.apirest.entity.Vehiculo;
import com.serviexpress.apirest.service.impl.VehiculoServicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/vehiculo")
public class VehiculoController {

	@Autowired
	@Qualifier("serviVehiculo")
	VehiculoServicesImpl vehiculoServicesImpl;


	// Cliente
	@PutMapping("/vehiculo")
	public ResponseEntity<?> agregarVehiculo(@RequestBody @Valid Vehiculo vehiculo) {
		return ResponseEntity.ok(vehiculoServicesImpl.crear(vehiculo));

	}

	@PostMapping("/vehiculo")
	public ResponseEntity<?> actualizarVehiculo(@RequestBody @Valid Vehiculo vehiculo) {
		return ResponseEntity.ok(vehiculoServicesImpl.actualizar(vehiculo));
	}

	@GetMapping(value = "/vehiculo")
	public List<Vehiculo> obtenerVehiculo(Pageable pageable,Long idCliente) {
		System.out.println("empleado.getNombre()");
		return vehiculoServicesImpl.obtenerPorPaginacion(pageable,idCliente);
	}

	@GetMapping(value = "/allvehiculo")
	public List<Vehiculo> allVehiculo() {
		return vehiculoServicesImpl.obtener();
	}


}