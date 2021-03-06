package com.serviexpress.apirest.controller;

import java.util.List;

import javax.validation.Valid;

import com.serviexpress.apirest.entity.Vehiculo;
import com.serviexpress.apirest.payload.response.VehiculoDTO;
import com.serviexpress.apirest.service.impl.VehiculoServicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public ResponseEntity<?> agregarVehiculo(@RequestBody @Valid VehiculoDTO vehiculoDTO) {
		Vehiculo vehiculo= new Vehiculo();
		vehiculo.setActive(vehiculoDTO.isActive());
		vehiculo.setAnio(vehiculoDTO.getAnio());
		vehiculo.setIdcliente(vehiculoDTO.getIdcliente());
		vehiculo.setMarca(vehiculoDTO.getMarca());
		vehiculo.setModelo(vehiculoDTO.getModelo());
		vehiculo.setNrochasis(vehiculoDTO.getNrochasis());
		vehiculo.setPatente(vehiculoDTO.getPatente());
		vehiculo.setTipovehiculo(vehiculoDTO.getTipovehiculo());
		return ResponseEntity.ok(vehiculoServicesImpl.crear(vehiculo));

	}

	@PostMapping("/vehiculo")
	public ResponseEntity<?> actualizarVehiculo(@RequestBody @Valid VehiculoDTO vehiculoDTO) {
		Vehiculo vehiculo= new Vehiculo();
		vehiculo.setActive(vehiculoDTO.isActive());
		vehiculo.setAnio(vehiculoDTO.getAnio());
		vehiculo.setIdcliente(vehiculoDTO.getIdcliente());
		vehiculo.setMarca(vehiculoDTO.getMarca());
		vehiculo.setModelo(vehiculoDTO.getModelo());
		vehiculo.setNrochasis(vehiculoDTO.getNrochasis());
		vehiculo.setPatente(vehiculoDTO.getPatente());
		vehiculo.setTipovehiculo(vehiculoDTO.getTipovehiculo());
		vehiculo.setIdvehiculo(vehiculoDTO.getIdvehiculo());
		return ResponseEntity.ok(vehiculoServicesImpl.actualizar(vehiculo));
	}

	@GetMapping(value = "/{idCliente}")
	public List<Vehiculo> obtenerVehiculo(Pageable pageable, @PathVariable(value = "idCliente") Long idCliente) {
		return vehiculoServicesImpl.obtenerPorPaginacion(pageable, idCliente);
	}

	@GetMapping(value = "/{idCliente}/allvehiculo")
	public List<Vehiculo> obtenerVehiculos( @PathVariable(value = "idCliente") Long idCliente) {
		return vehiculoServicesImpl.obtenerTodosPaginacion(idCliente);
	}

	@GetMapping(value = "/allvehiculo")
	public List<Vehiculo> allVehiculo(Pageable pageable) {
		return vehiculoServicesImpl.obtenerTodosPaginacion(pageable);
	}
}