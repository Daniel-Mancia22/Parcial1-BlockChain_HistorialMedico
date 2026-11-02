package blockchain;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa a un medico del catalogo (sin base de datos: es un arreglo
 * fijo en memoria). Cada medico tiene su especialidad y su numero de
 * la Junta de Vigilancia de la Profesion Medica (JVPM), que es el
 * registro profesional real que usan los medicos en El Salvador.
 */

public class Medico implements Serializable{
    private String nombre;
    private String especialidad;
    private String jvpm;

    public Medico(String pNombre, String pEspecialidad, String pJvpm){
        this.nombre = pNombre;
        this.especialidad = pEspecialidad;
        this.jvpm = pJvpm;
    }

    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getJvpm() { return jvpm; }

    @Override
    public String toString(){
        // Se usa tanto para el hash del bloque como para mostrarlo en el combo del formulario
        return "Dr(a). " + nombre + " - " + especialidad + " (JVPM " + jvpm + ")";
    }

    /** Concatenacion cruda para el hash (sin formato bonito). */
    public String toHashString(){
        return nombre + especialidad + jvpm;
    }

    // Catalogo fijo de medicos (simula lo que normalmente vendria de una base de datos). Se puede ampliar libremente.
    public static final Medico[] CATALOGO = new Medico[] {
        new Medico("Carlos Daniel Ramirez", "Medicina General", "4521"),
        new Medico("Ana Lucia Portillo", "Pediatria", "3390"),
        new Medico("Luis Fernando Melendez", "Cardiologia", "5871"),
        new Medico("Karla Alejandra Hidalgo", "Ginecologia", "2298"),
        new Medico("Jose Melvin Alas", "Traumatologia", "6120"),
        new Medico("Sofia Alexandra Contreras", "Dermatologia", "7745"),
    };

    // Tipos de diagnostico/consulta sugeridos por especialidad. Se usa
    // para poblar dinamicamente el combo "Tipo de registro" en el
    // formulario segun el medico que se elija.
    public static final Map<String, List<String>> TIPOS_POR_ESPECIALIDAD = new LinkedHashMap<>();
    static {
        TIPOS_POR_ESPECIALIDAD.put("Medicina General", List.of(
                "Consulta general", "Resfriado comun", "Control de rutina",
                "Referencia a especialista", "Certificado medico"));
        TIPOS_POR_ESPECIALIDAD.put("Pediatria", List.of(
                "Control de nino sano", "Vacunacion", "Desarrollo psicomotor",
                "Enfermedad respiratoria", "Alergia alimentaria"));
        TIPOS_POR_ESPECIALIDAD.put("Cardiologia", List.of(
                "Hipertension arterial", "Arritmia", "Electrocardiograma",
                "Control post-infarto", "Colesterol alto"));
        TIPOS_POR_ESPECIALIDAD.put("Ginecologia", List.of(
                "Control prenatal", "Papanicolaou", "Planificacion familiar",
                "Ecografia", "Menopausia"));
        TIPOS_POR_ESPECIALIDAD.put("Traumatologia", List.of(
                "Fractura", "Esguince", "Fisioterapia",
                "Dolor de espalda", "Cirugia ortopedica"));
        TIPOS_POR_ESPECIALIDAD.put("Dermatologia", List.of(
                "Acne", "Alergia cutanea", "Revision de lunares",
                "Psoriasis", "Cirugia menor de piel"));
    }
}
