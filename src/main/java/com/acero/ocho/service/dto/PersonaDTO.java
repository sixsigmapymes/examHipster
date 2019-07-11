package com.acero.ocho.service.dto;
import java.io.Serializable;
import java.util.Objects;
import com.acero.ocho.domain.enumeration.Sexo;

/**
 * A DTO for the {@link com.acero.ocho.domain.Persona} entity.
 */
public class PersonaDTO implements Serializable {

    private Long id;

    private String nombre;

    private String apellido;

    private Long edad;

    private Sexo sexo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getEdad() {
        return edad;
    }

    public void setEdad(Long edad) {
        this.edad = edad;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonaDTO personaDTO = (PersonaDTO) o;
        if (personaDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), personaDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PersonaDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", edad=" + getEdad() +
            ", sexo='" + getSexo() + "'" +
            "}";
    }
}
