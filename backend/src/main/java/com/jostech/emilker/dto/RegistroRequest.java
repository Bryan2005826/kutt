package com.jostech.emilker.dto;

public class RegistroRequest {
    private String nombre;
    private String correo;
    private String password;
    private String rol; // opcional: CLIENTE (por defecto) o ADMIN_NEGOCIO

    // ---- perfil de Cliente ----
    private String apellidos;
    private String telefono;
    private String departamento;
    private String ciudad;
    private String genero;
    private String fechaNacimiento;
    private String direccion;
    private boolean permiteUbicacion;

    // ---- datos del negocio (rol ADMIN_NEGOCIO) ----
    private String nombreNegocio;
    private String rubro;
    private String telefonoFijoNegocio;
    private String departamentoNegocio;
    private String direccionNegocio;
    private String logoUrl;
    private String portadaUrl;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isPermiteUbicacion() { return permiteUbicacion; }
    public void setPermiteUbicacion(boolean permiteUbicacion) { this.permiteUbicacion = permiteUbicacion; }

    public String getNombreNegocio() { return nombreNegocio; }
    public void setNombreNegocio(String nombreNegocio) { this.nombreNegocio = nombreNegocio; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public String getTelefonoFijoNegocio() { return telefonoFijoNegocio; }
    public void setTelefonoFijoNegocio(String telefonoFijoNegocio) { this.telefonoFijoNegocio = telefonoFijoNegocio; }

    public String getDepartamentoNegocio() { return departamentoNegocio; }
    public void setDepartamentoNegocio(String departamentoNegocio) { this.departamentoNegocio = departamentoNegocio; }

    public String getDireccionNegocio() { return direccionNegocio; }
    public void setDireccionNegocio(String direccionNegocio) { this.direccionNegocio = direccionNegocio; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }
}
