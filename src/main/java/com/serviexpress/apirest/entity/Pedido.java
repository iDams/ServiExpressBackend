package com.serviexpress.apirest.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Table(name = "Pedido", uniqueConstraints = { @UniqueConstraint(columnNames = { "idpedido" }) })
@Entity
public class Pedido implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idpedido;
	private Long empleado;
	private Long proveedor;
	private Long producto;
	private String cantidad;
	private Date fechapedido;
	private Date fecharecibo;
	private int estado;

	public Pedido() {
	}

	public Long getIdpedido() {
		return idpedido;
	}

	public void setIdpedido(Long idpedido) {
		this.idpedido = idpedido;
	}

	public Long getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Long empleado) {
		this.empleado = empleado;
	}

	public Long getProveedor() {
		return proveedor;
	}

	public void setProveedor(Long proveedor) {
		this.proveedor = proveedor;
	}

	public Long getProducto() {
		return producto;
	}

	public void setProducto(Long producto) {
		this.producto = producto;
	}

	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}

	public Date getFechapedido() {
		return fechapedido;
	}

	public void setFechapedido(Date fechapedido) {
		this.fechapedido = fechapedido;
	}

	public Date getFecharecibo() {
		return fecharecibo;
	}

	public void setFecharecibo(Date fecharecibo) {
		this.fecharecibo = fecharecibo;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public Pedido(Pedido pedido) {
		this.idpedido = pedido.idpedido;
		this.empleado = pedido.empleado;
		this.proveedor = pedido.proveedor;
		this.proveedor = pedido.proveedor;
		this.producto = pedido.producto;
		this.cantidad = pedido.cantidad;
		this.fechapedido = pedido.fechapedido;
		this.fecharecibo = pedido.fecharecibo;
		this.estado = pedido.estado;
	}

	@Override
    public String toString() {
        return "Pedido [producto=" + producto + ", proveedor=" + proveedor + ", empelado=" + empleado
                + ", cantidad=" + cantidad + ", fechapedido=" + fechapedido + ", fecharecibo" + fecharecibo + ", estado=" + estado + "]";
    }

	public Pedido(Long idpedido, Long empleado, Long proveedor, Long producto, String cantidad, Date fechapedido,
			Date fecharecibo, int estado) {
		this.idpedido = idpedido;
		this.empleado = empleado;
		this.proveedor = proveedor;
		this.producto = producto;
		this.cantidad = cantidad;
		this.fechapedido = fechapedido;
		this.fecharecibo = fecharecibo;
		this.estado = estado;
	}

	

}