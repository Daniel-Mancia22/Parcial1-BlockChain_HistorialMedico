package blockchain;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa un evento clínico dentro de un bloque: un diagnóstico,
 * tratamiento, vacuna, cirugía, examen o alergia asociado a un paciente y registrado por un médico.
 *
 * Registros nuevos (encriptado=true): todos los campos sensibles se guardan cifrados con la llave de 
 * sesion de BlockChain y solo queda en claro duiIndex (SHA-256 del DUI + sal), que permite buscar sin exponer el DUI.
 * Registros viejos (encriptado=false): campos en claro, duiIndex nulo.
 */

public class RegistroMedico implements Serializable{
    private int id;
    private long timeStamp;
    private Paciente paciente;
    private Medico medico;
    private String tipoRegistro;
    private String descripcion;
    private boolean encriptado;
    private String duiIndex;

    public RegistroMedico(int pId, Paciente pPaciente, Medico pMedico,
                           String pTipoRegistro, String pDescripcion, boolean pEncriptado){
        this.id = pId;
        this.timeStamp = new Date().getTime();
        this.paciente = pPaciente;
        this.medico = pMedico;
        this.tipoRegistro = pTipoRegistro;
        this.descripcion = pDescripcion;
        this.encriptado = pEncriptado;
        this.duiIndex = null;
    }

    /**
     * Fabrica transparente: cifra todos los campos sensibles con la llave de
     * sesion de la cadena y calcula el indice hash del DUI. No pide password.
     */
    public static RegistroMedico crearCifrado(int pId, Paciente pPaciente, Medico pMedico,
                                              String pTipoRegistro, String pDescripcion,
                                              BlockChain bc) throws Exception{
        Paciente pEnc = new Paciente(
                bc.cifrarCampo(pPaciente.getNombres()),
                bc.cifrarCampo(pPaciente.getApellidos()),
                bc.cifrarCampo(pPaciente.getDui()),
                bc.cifrarCampo(pPaciente.getFechaNacimiento()),
                bc.cifrarCampo(pPaciente.getTipoSangre()),
                bc.cifrarCampo(pPaciente.getFamiliarResponsable()),
                bc.cifrarCampo(pPaciente.getDuiResponsable()),
                bc.cifrarCampo(pPaciente.getParentesco()),
                bc.cifrarCampo(pPaciente.getTelefonoResponsable()),
                bc.cifrarCampo(pPaciente.getDireccionResponsable())
        );
        
        Medico mEnc = new Medico(
                bc.cifrarCampo(pMedico.getNombre()),
                bc.cifrarCampo(pMedico.getEspecialidad()),
                bc.cifrarCampo(pMedico.getJvpm())
        );
        
        String tipoEnc = bc.cifrarCampo(pTipoRegistro);
        String descEnc = bc.cifrarCampo(pDescripcion);
        RegistroMedico r = new RegistroMedico(pId, pEnc, mEnc, tipoEnc, descEnc, true);
        r.setDuiIndex(bc.indiceDui(pPaciente.getDui()));
        
        return r;
    }

    private static String seguro(BlockChain bc, String blob){
        try{
            String v = bc.descifrarCampo(blob);
            return v == null ? "" : v;
        } catch (Exception e){
            return "[ERROR DESCIFRADO]";
        }
    }

    public Paciente getPacienteDescifrado(BlockChain bc){
        if (!encriptado || bc == null) return paciente;
        
        return new Paciente(
                seguro(bc, paciente.getNombres()),
                seguro(bc, paciente.getApellidos()),
                seguro(bc, paciente.getDui()),
                seguro(bc, paciente.getFechaNacimiento()),
                seguro(bc, paciente.getTipoSangre()),
                seguro(bc, paciente.getFamiliarResponsable()),
                seguro(bc, paciente.getDuiResponsable()),
                seguro(bc, paciente.getParentesco()),
                seguro(bc, paciente.getTelefonoResponsable()),
                seguro(bc, paciente.getDireccionResponsable())
        );
    }

    public Medico getMedicoDescifrado(BlockChain bc){
        if (!encriptado || bc == null) return medico;
        
        return new Medico(
                seguro(bc, medico.getNombre()),
                seguro(bc, medico.getEspecialidad()),
                seguro(bc, medico.getJvpm())
        );
    }

    public String getTipoRegistroDescifrado(BlockChain bc){
        if (!encriptado || bc == null) return tipoRegistro;
        return seguro(bc, tipoRegistro);
    }

    public String getDescripcionDescifrada(BlockChain bc){
        if (!encriptado || bc == null) return descripcion;
        return seguro(bc, descripcion);
    }

    @Override
    public String toString() {
        // Esta cadena es la que entra al hash del bloque (Block.toString()).
        // En registros cifrados contiene blobs + indice, nunca texto claro.
        return Integer.toString(id) +
               Long.toString(timeStamp) +
               paciente.toString() + medico.toHashString() + tipoRegistro + descripcion +
               (duiIndex == null ? "" : duiIndex);
    }

    // Getter and Setter
    public long getTimeStamp() { return timeStamp; }

    public Paciente getPaciente() { return paciente; }

    public Medico getMedico() { return medico; }

    public String getTipoRegistro() { return tipoRegistro; }

    public String getDescripcion() { return descripcion; }

    public boolean isEncriptado() { return encriptado; }

    public String getDuiIndex() { return duiIndex; }

    public void setDuiIndex(String duiIndex) { this.duiIndex = duiIndex; }
}
