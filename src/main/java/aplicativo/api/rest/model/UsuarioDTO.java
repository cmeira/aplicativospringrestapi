package aplicativo.api.rest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable{
	
	private String usuarioLogin;
	private String usuarioNome;
	private String usuarioCPF;
	
	public UsuarioDTO (Usuario usuario) {
		this.usuarioLogin = usuario.getLogin();
		this.usuarioNome = usuario.getNome();
		this.usuarioCPF = usuario.getCpf();
	}
	
	public String getUsuarioLogin() {
		return usuarioLogin;
	}
	public void setUsuarioLogin(String usuarioLogin) {
		this.usuarioLogin = usuarioLogin;
	}
	public String getUsuarioNome() {
		return usuarioNome;
	}
	public void setUsuarioNome(String usuarioNome) {
		this.usuarioNome = usuarioNome;
	}
	public String getUsuarioCPF() {
		return usuarioCPF;
	}
	public void setUsuarioCPF(String usuarioCPF) {
		this.usuarioCPF = usuarioCPF;
	}
	
	

}
