package blockchain;

import java.io.Serializable;

public class Paciente implements Serializable
{
    private String nombres;
    private String apellidos;
    private String dui;                 // DUI del paciente. Formato El Salvador: NNNNNNNN-N
    private String fechaNacimiento;     // dd/MM/yyyy
    private String tipoSangre;          // O+, O-, A+, A-, B+, B-, AB+, AB-, Pendiente de examen, Desconocido

    private String familiarResponsable; // Nombre completo del responsable
    private String duiResponsable;      // DUI del responsable. Formato: NNNNNNNN-N
    private String parentesco;          // Padre, Madre, Conyuge, Hijo/a, Hermano/a, Otro
    private String telefonoResponsable; // Formato: NNNN-NNNN
    private String direccionResponsable;

    public Paciente(String pNombres, String pApellidos, String pDui, String pFechaNacimiento,
                     String pTipoSangre, String pFamiliarResponsable, String pDuiResponsable,
                     String pParentesco, String pTelefonoResponsable, String pDireccionResponsable){
        this.nombres = pNombres;
        this.apellidos = pApellidos;
        this.dui = pDui;
        this.fechaNacimiento = pFechaNacimiento;
        this.tipoSangre = pTipoSangre;
        this.familiarResponsable = pFamiliarResponsable;
        this.duiResponsable = pDuiResponsable;
        this.parentesco = pParentesco;
        this.telefonoResponsable = pTelefonoResponsable;
        this.direccionResponsable = pDireccionResponsable;
    }

    // Valida el formato NNNNNNNN-N (8 digitos, guion, 1 digito). No valida el digito verificador real.
    public static boolean esFormatoDuiValido(String pDui){
        return pDui != null && pDui.matches("\\d{8}-\\d");
    }

    // Valida el formato de telefono salvadoreno: NNNN-NNNN
    public static boolean esFormatoTelefonoValido(String pTelefono){
        return pTelefono != null && pTelefono.matches("\\d{4}-\\d{4}");
    }

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    @Override
    public String toString(){
        // Concatenacion usada para el hash del bloque (Block.toString() -> RegistroMedico.toString())
        return nombres + apellidos + dui + fechaNacimiento + tipoSangre
                + familiarResponsable + duiResponsable + parentesco + telefonoResponsable + direccionResponsable;
    }

    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDui() { return dui; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getTipoSangre() { return tipoSangre; }
    public String getFamiliarResponsable() { return familiarResponsable; }
    public String getDuiResponsable() { return duiResponsable; }
    public String getParentesco() { return parentesco; }
    public String getTelefonoResponsable() { return telefonoResponsable; }
    public String getDireccionResponsable() { return direccionResponsable; }
}
